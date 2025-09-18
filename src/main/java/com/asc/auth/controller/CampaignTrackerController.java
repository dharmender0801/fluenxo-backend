package com.asc.auth.controller;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/track")
@Tag(name = "Campaign Tracker Controller", description = "Campaign Management Api's")
public class CampaignTrackerController {

	@GetMapping("/{campaignId}/click")
	public ResponseEntity<Map<String, Object>> trackClick(@PathVariable Long campaignId,
			@RequestParam(required = false) Long userId, HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		Map<String, String> headersMap = new HashMap<>();
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			headersMap.put(headerName, request.getHeader(headerName));
		}

		String ip = request.getHeader("X-Forwarded-For");
		if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
			ip = ip.split(",")[0].trim();
		} else {
			ip = request.getHeader("X-Real-IP");
			if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
			}
		}
		String referrer = request.getHeader("Referer");
		String userAgent = request.getHeader("User-Agent");
		String deviceType = (userAgent != null && userAgent.toLowerCase().contains("mobile")) ? "MOBILE" : "DESKTOP";
		response.put("campaignId", campaignId);
		response.put("userId", userId);
		response.put("ip", ip);
		response.put("referrer", referrer);
		response.put("userAgent", userAgent);
		response.put("deviceType", deviceType);
		response.put("headers", headersMap);

		return ResponseEntity.ok(response);
	}

}
