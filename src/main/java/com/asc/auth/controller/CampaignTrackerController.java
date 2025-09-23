package com.asc.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.asc.auth.dto.CampaignClickInfoDto;
import com.asc.auth.service.CampaignService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/track")
@Tag(name = "Campaign Tracker Controller", description = "Campaign Management Api's")
public class CampaignTrackerController {

	@Autowired
	CampaignService campaignService;

	@GetMapping("/{campaignId}/click")
	public String trackClick(@PathVariable Long campaignId, @RequestParam(required = false) Long userId,
			HttpServletRequest request, Model model) {
		String redirectUrl = campaignService.getRedirectUrl(campaignId, userId, request, model);
		model.addAttribute("redirectUrl", redirectUrl);
		return "Home";
	}

	@PostMapping("/updateClick")
	public ResponseEntity<String> updateDevice(@RequestBody CampaignClickInfoDto campaignClickInfoDto) {
		log.info("Request for Update : {} ", campaignClickInfoDto);
		campaignService.updateCampaignClick(campaignClickInfoDto);
		return new ResponseEntity<String>("Success", HttpStatus.OK);
	}

}
