package com.asc.auth.service;

import java.util.ArrayList;
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
import org.springframework.stereotype.Service;

import com.asc.auth.dto.CampaignInfoDto;
import com.asc.auth.dto.FiltersDto;
import com.asc.auth.exception.RecordNotFoundException;
import com.asc.auth.model.AssociateUser;
import com.asc.auth.model.CampaignInfo;
import com.asc.auth.repository.CampaignInfoRepository;
import com.asc.auth.transformer.CampaignInfoFiltersTransformer;
import com.asc.auth.utils.Utils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CampaignService {

	@Autowired
	CampaignInfoRepository campaignInfoRepository;

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

}
