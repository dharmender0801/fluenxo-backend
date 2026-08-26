package com.asc.auth.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.asc.auth.dto.ParsedCommandDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class IntentService {
	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;

	@Value("${ollama.api.key:0e16ecfe1bc945b29bef8931638e4a6e.v7u5XBFBtUizKrRNSav8etC5}")
	private String ollamaApiKey;

	@Value("${ollama.base.url:https://ollama.com}")
	private String ollamaBaseUrl;

	@Value("${ollama.model:gpt-oss:20b-cloud}")
	private String ollamaModel;

	private static final String SYSTEM_PROMPT = """
			Parse command to JSON only. Types: OPEN_APP, OPEN_WEBSITE, CALL_CONTACT, SEND_SMS, SEND_WHATSAPP, SEARCH_WHATSAPP, SEARCH_YOUTUBE, SEARCH_MAPS, WEB_SEARCH, SET_TIMER, UNKNOWN
			Priority:
			whatsapp+msg→SEND_WHATSAPP | whatsapp+search→SEARCH_WHATSAPP | sms/msg→SEND_SMS | call→CALL_CONTACT | youtube→SEARCH_YOUTUBE | maps→SEARCH_MAPS | timer→SET_TIMER (sec) | app+open→OPEN_APP | website→OPEN_WEBSITE | search→WEB_SEARCH | else UNKNOWN
			Keep names/messages exact. duration: min*60, ghante*3600. confidence 0-1.
			ONLY return:
			{"type":"","contactName":null,"message":null,"query":null,"durationSeconds":null,"appName":null,"websiteName":null,"url":null,"rawText":"","confidence":"0.0"}
			""";

	public ParsedCommandDto parseCommand(String command) {
		try {
			String url = ollamaBaseUrl.replaceAll("/$", "") + "/api/chat";
			Map<String, Object> requestBody = new HashMap<>();
			requestBody.put("model", ollamaModel);
			requestBody.put("stream", false);
			requestBody.put("format", "json");
			Map<String, Object> options = new HashMap<>();
			options.put("temperature", 0.0);
			requestBody.put("options", options);
			List<Map<String, String>> messages = new ArrayList<>();
			messages.add(Map.of("role", "system", "content", SYSTEM_PROMPT));
			messages.add(Map.of("role", "user", "content", command));
			requestBody.put("messages", messages);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			if (ollamaApiKey != null && !ollamaApiKey.isBlank()) {
				headers.setBearerAuth(ollamaApiKey);
			}
			HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
			log.info("Calling Ollama for command: {}", command);
			ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
			if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
				Map<String, Object> body = response.getBody();
				Map<String, Object> message = (Map<String, Object>) body.get("message");
				if (message != null && message.get("content") != null) {
					String content = message.get("content").toString().trim();
					log.info("Ollama raw response: [{}]", content);
					try {
						ParsedCommandDto parsed = objectMapper.readValue(content, ParsedCommandDto.class);
						if (parsed.getRawText() == null) {
							parsed.setRawText(command);
						}
						return parsed;
					} catch (Exception jsonException) {
						log.error("Invalid JSON received from Ollama: [{}]", content, jsonException);
						return unknown(command);
					}
				}
			}
			log.warn("Invalid or empty response received from Ollama");
			return unknown(command);
		} catch (Exception e) {
			log.error("Error while parsing command with Ollama: {}", e.getMessage(), e);
			return unknown(command);
		}
	}

	private ParsedCommandDto unknown(String command) {
		ParsedCommandDto dto = new ParsedCommandDto();
		dto.setType("UNKNOWN");
		dto.setRawText(command);
		dto.setConfidence(0.0);
		return dto;
	}
}
