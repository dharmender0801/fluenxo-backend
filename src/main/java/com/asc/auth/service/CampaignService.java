package com.asc.auth.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.asc.auth.dto.CampaignClickInfoDto;
import com.asc.auth.dto.CampaignInfoDto;
import com.asc.auth.dto.CampaignInfoDto.AssociateUserDto;
import com.asc.auth.dto.FiltersDto;
import com.asc.auth.exception.RecordNotFoundException;
import com.asc.auth.model.AssociateUser;
import com.asc.auth.model.CampaignClickInfo;
import com.asc.auth.model.CampaignInfo;
import com.asc.auth.model.enums.CampaignStatus;
import com.asc.auth.model.enums.TransactionType;
import com.asc.auth.repository.CampaignClickInfoRepository;
import com.asc.auth.repository.CampaignInfoRepository;
import com.asc.auth.transformer.CampaignInfoFiltersTransformer;
import com.asc.auth.utils.Constants;
import com.asc.auth.utils.Utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CampaignService {

	@Autowired
	CampaignInfoRepository campaignInfoRepository;

	@Autowired
	CampaignClickInfoRepository campaignClickInfoRepository;

	@Autowired
	WalletService walletService;

	public CampaignInfoDto addOrUpdate(CampaignInfoDto campaignInfoDto) {
		if (Objects.nonNull(campaignInfoDto.getId())) {
			CampaignInfo campaignInfo = campaignInfoRepository.findById(campaignInfoDto.getId())
					.orElseThrow(() -> new RecordNotFoundException("No Campaign found "));
			Utils.copyProperties(campaignInfoDto, campaignInfo);
			log.info("adding User : {} ", associateUser(campaignInfoDto, campaignInfo));
			return copyEntitytoDto(saveCampaign(campaignInfo));
		} else {
			CampaignInfo campaignInfo = new CampaignInfo();
			Utils.copyProperties(campaignInfoDto, campaignInfo);
			return copyEntitytoDto(saveCampaign(campaignInfo));
		}
	}

	private Object associateUser(CampaignInfoDto campaignInfoDto, CampaignInfo campaignInfo) {
		log.info("User : {} ", campaignInfoDto.getAssociatedUser());
		Map<Long, AssociateUser> existingUserMap = campaignInfo.getAssociatedUsers().stream()
				.collect(Collectors.toMap(AssociateUser::getId, Function.identity()));
		return campaignInfoDto.getAssociatedUser().stream().map(user -> {
			AssociateUser associateUser;
			if (user.getId() != null) {
				associateUser = existingUserMap.get(user.getId());
				Utils.copyProperties(user, associateUser);
				return associateUser;
			} else {
				associateUser = new AssociateUser();
				Utils.copyProperties(user, associateUser);
				associateUser.setCampaign(campaignInfo);
				campaignInfo.getAssociatedUsers().add(associateUser);
				return associateUser;
			}
		}).collect(Collectors.toList());
	}

	private CampaignInfoDto copyEntitytoDto(CampaignInfo saveCampaign) {
		CampaignInfoDto campaignInfoDto = new CampaignInfoDto();
		Utils.copyProperties(saveCampaign, campaignInfoDto);
		List<AssociateUserDto> associateUserDtos = saveCampaign.getAssociatedUsers().stream().map(user -> {
			AssociateUserDto associateUserDto = new AssociateUserDto();
			Utils.copyProperties(user, associateUserDto);
			return associateUserDto;
		}).collect(Collectors.toList());
		campaignInfoDto.setAssociatedUser(associateUserDtos);
		return campaignInfoDto;
	}

	private CampaignInfo saveCampaign(CampaignInfo campaignInfo) {
		return campaignInfoRepository.save(campaignInfo);
	}

	public Page<CampaignInfoDto> getCampaigns(List<FiltersDto> filters, Integer pageNumber, Integer pageSize,
			String sortingColumn, Direction direction) {
		Sort sorting = (Boolean.TRUE.equals(Objects.nonNull(sortingColumn))
				&& Boolean.TRUE.equals(Objects.nonNull(direction))) ? Sort.by(direction, sortingColumn)
						: Sort.by(Direction.ASC, "id");
		filters = (Boolean.FALSE.equals(filters.isEmpty())) ? filters : new ArrayList<>();
		pageSize = (Boolean.FALSE.equals(pageSize < 1)) ? pageSize : 5;
		Page<CampaignInfo> pageList = campaignInfoRepository.findAll(
				CampaignInfoFiltersTransformer.buildCriteria(filters), PageRequest.of(pageNumber, pageSize, sorting));
		if (!pageList.isEmpty()) {
			List<CampaignInfoDto> userDetail = pageList.stream().map(this::copyEntitytoDto)
					.collect(Collectors.toList());
			Page<CampaignInfoDto> pageData = new PageImpl<>(userDetail, PageRequest.of(pageNumber, pageSize, sorting),
					pageList.getTotalElements());
			return pageData;

		}
		return null;
	}

	public String getRedirectUrl(Long campaignId, Long userId, HttpServletRequest request, Model model) {
		CampaignInfo campaignInfo = campaignInfoRepository.findById(campaignId)
				.orElseThrow(() -> new RecordNotFoundException("No Campaign found "));
		if (CampaignStatus.PAUSE.equals(campaignInfo.getStatus())
				|| CampaignStatus.INACTIVE.equals(campaignInfo.getStatus())) {
			log.info("Campaign is not active. Skipping click update for campaignId: {}", campaignInfo.getId());
			return "Pause";
		}

		String ip = request.getHeader("X-Forwarded-For");
		log.info("IP :{} ", ip);
		if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
			ip = ip.split(",")[0].trim();
		} else {
			ip = request.getHeader("X-Real-IP");
			log.info("IP :{} ", ip);
			if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
			}
		}
		log.info("IP :{} ", ip);
		String referrer = request.getHeader("Referer");
		String userAgent = request.getHeader("User-Agent");
		String deviceType = (userAgent != null && userAgent.toLowerCase().contains("mobile")) ? "MOBILE" : "DESKTOP";
		saveTrackings(campaignInfo, userId, request.getRemoteAddr(), referrer, userAgent, deviceType, model);
		model.addAttribute("redirectUrl", campaignInfo.getCampaignLink());
//		return campaignInfo.getCampaignLink();
		return "Home";
	}

	private void saveTrackings(CampaignInfo campaignInfo, Long userId, String ip, String referrer, String userAgent,
			String deviceType, Model model) {
		CampaignClickInfo campaignClickInfo = new CampaignClickInfo();
		campaignClickInfo.setCampaignId(campaignInfo.getId());
		campaignClickInfo.setDeviceId(deviceType);
		campaignClickInfo.setInfluencerId(userId);
		campaignClickInfo.setIpAddress(ip);
		campaignClickInfo.setReferer(referrer);
		campaignClickInfo.setUserAgent(userAgent);
		campaignClickInfo = campaignClickInfoRepository.save(campaignClickInfo);
		log.info("saving Request : {} ", campaignClickInfo);
		model.addAttribute("clickId", campaignClickInfo.getId());

	}

	@Async
	public void updateCampaignClick(CampaignClickInfoDto campaignClickInfoDto) {
		CampaignClickInfo campaignClickInfo = campaignClickInfoRepository.findById(campaignClickInfoDto.getId())
				.orElseThrow(() -> new RecordNotFoundException("No Campaign Click Found "));
		Utils.copyProperties(campaignClickInfoDto, campaignClickInfo);
		log.info("updating click request : {} ", campaignClickInfoRepository.save(campaignClickInfo));
		long count = campaignClickInfoRepository.countByIpAddressAndDeviceId(campaignClickInfoDto.getIpAddress(),
				campaignClickInfoDto.getDeviceId());
		if (count == 1) {
			CampaignInfo campaignInfo = campaignInfoRepository.findById(campaignClickInfo.getCampaignId())
					.orElseThrow(() -> new RecordNotFoundException("No Campaign found "));
			Map<Long, AssociateUser> userMap = campaignInfo.getAssociatedUsers().stream()
					.collect(Collectors.toMap(AssociateUser::getUserId, Function.identity()));
			AssociateUser associateUser = userMap.get(campaignClickInfo.getInfluencerId());
			if (associateUser == null) {
				log.warn("Influencer not associated with campaign: {}", campaignClickInfo.getInfluencerId());
				return;
			}
			BigDecimal costForBrands = campaignInfo.getCpc();
			BigDecimal influencerShare = associateUser.getCpc() != null ? associateUser.getCpc()
					: campaignInfo.getCpc().multiply(new BigDecimal("0.2"));
			BigDecimal platformMargin = costForBrands.subtract(influencerShare);
			try {
				walletService.applyTransaction(campaignInfo.getCreatedBy(), costForBrands, TransactionType.DEBIT,
						"Deducted : " + campaignInfo.getId());
				walletService.applyTransaction(campaignClickInfo.getInfluencerId(), influencerShare,
						TransactionType.CREDIT, "Influencer reward");
				walletService.applyTransaction(Constants.ZERO_LONG, platformMargin, TransactionType.CREDIT,
						"Platform margin");
			} catch (Exception e) {
				campaignInfo.setStatus(CampaignStatus.PAUSE);
				campaignInfo.setRemarks("Campaign Paused Due to : " + e.getMessage());
				log.info("Campaign Pause : {} ", campaignInfoRepository.save(campaignInfo));
			}

		} else {
			log.info("Duplicate click detected for IP: {}, UA: {}. Skipping deduction.",
					campaignClickInfoDto.getIpAddress(), campaignClickInfoDto.getUserAgent());
		}

	}

	public Map<String, Object> getJson() {
		List<Object[]> rows = campaignClickInfoRepository.getAllMetrics();
		Map<String, List<Map<String, Object>>> result = new HashMap<>();
		result.put("campaign_totals", new ArrayList<>());
		result.put("influencer_totals", new ArrayList<>());
		result.put("device_totals", new ArrayList<>());
		result.put("city_totals", new ArrayList<>());
		for (Object[] row : rows) {
			String type = (String) row[0];
			Map<String, Object> map = new HashMap<>();
			map.put("campaign_id", row[1]);
			map.put("influencer_id", row[2]);
			map.put("city", row[3]);
			map.put("device", row[4]);
			map.put("total_clicks", row[5]);
			map.put("total_reach", row[6]);

			switch (type) {
			case "campaign_total":
				result.get("campaign_totals").add(map);
				break;
			case "influencer_total":
				result.get("influencer_totals").add(map);
				break;
			case "device_total":
				result.get("device_totals").add(map);
				break;
			case "city_total":
				result.get("city_totals").add(map);
				break;
			}
		}
		return (Map) result;
	}

}
