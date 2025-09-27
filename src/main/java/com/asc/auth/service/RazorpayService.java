package com.asc.auth.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.asc.auth.model.PaymentLog;
import com.asc.auth.model.enums.TransactionType;
import com.asc.auth.repository.PaymentLogRepository;
import com.asc.auth.security.TokenFilter;
import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RazorpayService {

	private final RazorpayClient client;
	@Autowired
	PaymentLogRepository paymentLogRepository;
	@Value("${razorpay.key.id}")
	String razorpaykeys;

	@Value("${razorpay.secret.key}")
	String razorpaySecreteValue;

	public RazorpayService(@Value("${razorpay.key.id}") String razorpayKey,
			@Value("${razorpay.secret.key}") String razorpaySecret) throws RazorpayException {
		this.client = new RazorpayClient(razorpayKey, razorpaySecret);
	}

	public Map<String, Object> createOrder(BigDecimal amount, String currency) throws RazorpayException {

		JSONObject options = new JSONObject();
		options.put("amount", amount.multiply(BigDecimal.valueOf(100)));
		options.put("currency", currency);
		options.put("payment_capture", 1);
		Order order = client.orders.create(options);

		PaymentLog log = new PaymentLog();
		log.setOrderId(order.get("id"));
		log.setAmount(amount);
		log.setUserId(TokenFilter.getGetUserId());
		log.setStatus(TransactionType.PENDING);
		log = paymentLogRepository.save(log);

		Map<String, Object> response = new HashMap<>();
		response.put("orderId", order.get("id"));
		response.put("amount", amount);
		response.put("currency", currency);
		response.put("key", razorpaykeys);
		response.put("id", log.getId());
		return response;
	}

	public boolean verifyPayment(String orderId, String paymentId, String paymentSignature) throws Exception {
		String generatedSignature = calculateSignature(orderId, paymentId);
		log.info("Generated Value : {} , Payment Signature : {}", generatedSignature, paymentSignature);
		return generatedSignature.equals(paymentSignature);

	}

	private String calculateSignature(String orderId, String paymentId) throws Exception {
		String payload = orderId + "|" + paymentId;
		Mac mac = Mac.getInstance("HmacSHA256");
		SecretKeySpec secretKeySpec = new SecretKeySpec(razorpaySecreteValue.getBytes(), "HmacSHA256");
		mac.init(secretKeySpec);
		byte[] hash = mac.doFinal(payload.getBytes());
		return Hex.encodeHexString(hash);
	}
}
