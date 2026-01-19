package com.example.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
@Service
public class Email {
	@Autowired
	private JavaMailSender mailSender;
	public void sendVerificationCode(String email, String code ) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setSubject("Verification code for insurance system");
		message.setText("Your verification code is:  " + code);
		mailSender.send(message);
	}
public void sendInvoice(String toEmail, byte[] pdfBytes) throws Exception {
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
    helper.setTo(toEmail);
    helper.setSubject("Your Insurance Invoice");
    helper.setText("Thank you for your purchase. Your invoice is attached.");
    ByteArrayResource pdfResource = new ByteArrayResource(pdfBytes);
    helper.addAttachment("invoice.pdf", pdfResource, "application/pdf");
    mailSender.send(message);
}
}
