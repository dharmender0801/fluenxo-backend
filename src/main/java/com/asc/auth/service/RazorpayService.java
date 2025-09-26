package com.asc.auth.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

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

	public Payment fetchPayment(String paymentId) throws RazorpayException {
		return client.payments.fetch(paymentId);
	}
}
