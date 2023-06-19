package com.vima.reservation.util.email.impl;

import com.vima.reservation.util.email.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

	@Autowired
	private JavaMailSender mailSender;
	@Value("${spring.mail.username}") private String sender;
	@Override
	@Async
	public void sendSimpleMail(String to, String subject, String body) {
		try {
			SimpleMailMessage mailMessage = new SimpleMailMessage();

			mailMessage.setFrom(sender);
			mailMessage.setTo(to);
			mailMessage.setText(body);
			mailMessage.setSubject(subject);

			mailSender.send(mailMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
