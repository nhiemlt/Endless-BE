package com.datn.endless.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private final JavaMailSender javaMailSender;

    public MailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendHtmlMail(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);

        javaMailSender.send(mimeMessage);
    }

    public void sendCancelOrder(String username, long orderID, String to) throws MessagingException {
        String subject = "Order Cancellation Confirmation";
        String htmlBody = "<p>Dear "+username+",</p>"
                + "<p>We regret to inform you that your order with order ID <strong>" + orderID + "</strong> has been canceled successfully.</p>"
                + "<p>If you have any questions or concerns regarding this cancellation, please feel free to contact our customer support team.</p>"
                + "<p>Thank you for your understanding.</p>"
                + "<p>Best regards,<br> Endless</p>";

        sendHtmlMail(to, subject, htmlBody);
    }

    public void sendForgotPasswordMail(String username, String to, String resetLink) throws MessagingException {
        String subject = "Password Reset Request";


        String htmlBody = "<p>Dear "+username+",</p>"
                + "<p>We received a request to reset the password for your account.</p>"
                + "<p>If you made this request, please click the link below to receive a temporary password:</p>"
                + "<p><a href=" + resetLink + ">Reset Password</a></p>"
                + "<p>If you did not request a password reset, please ignore this email or contact our support team.</p>"
                + "<p>Thank you for your attention.</p>"
                + "<p>Best regards,<br> Endless</p>";

        sendHtmlMail(to, subject, htmlBody);
    }

    public void sendTemporaryPasswordMail(String username, String to, String temporaryPassword) throws MessagingException {
        String subject = "Your Temporary Password";
        String htmlBody = "<p>Dear "+username+",</p>"
                + "<p>As requested, we have generated a temporary password for your account:</p>"
                + "<p><strong>" + temporaryPassword + "</strong></p>"
                + "<p>Please use this password to log in and reset your password immediately.</p>"
                + "<p>If you did not request this, please contact our support team as soon as possible.</p>"
                + "<p>Thank you for your attention.</p>"
                + "<p>Best regards,<br> Endless</p>";

        sendHtmlMail(to, subject, htmlBody);
    }


}
