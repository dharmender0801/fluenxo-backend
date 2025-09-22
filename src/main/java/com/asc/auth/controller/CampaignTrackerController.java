package com.asc.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.asc.auth.service.CampaignService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/track")
@Tag(name = "Campaign Tracker Controller", description = "Campaign Management Api's")
public class CampaignTrackerController {

	@Autowired
	CampaignService campaignService;

	@GetMapping("/{campaignId}/click")
	public ResponseEntity<String> trackClick(@PathVariable Long campaignId, @RequestParam(required = false) Long userId,
			HttpServletRequest request) {
		String redirectUrl = campaignService.getRedirectUrl(campaignId, userId, request);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Location", redirectUrl);
		return new ResponseEntity<>(headers, HttpStatus.FOUND);
	}

}
