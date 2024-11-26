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
        String subject = "Xác Nhận Hủy Đơn Hàng";
        String htmlBody = "<p>Chào " + username + ",</p>"
                + "<p>Chúng tôi xin thông báo rằng đơn hàng của bạn với mã đơn hàng <strong>" + orderID + "</strong> đã được hủy thành công.</p>"
                + "<p>Nếu bạn có bất kỳ câu hỏi hay thắc mắc nào liên quan đến việc hủy đơn hàng, vui lòng liên hệ với bộ phận hỗ trợ khách hàng của chúng tôi.</p>"
                + "<p>Cảm ơn bạn đã hiểu và thông cảm.</p>"
                + "<p>Trân trọng,<br> Endless</p>";

        sendHtmlMail(to, subject, htmlBody);
    }

    public void sendForgotPasswordMail(String username, String to, String resetLink) throws MessagingException {
        String subject = "Yêu Cầu Đặt Lại Mật Khẩu";
        String htmlBody = "<p>Chào " + username + ",</p>"
                + "<p>Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn.</p>"
                + "<p>Nếu bạn đã yêu cầu, vui lòng nhấp vào liên kết dưới đây để nhận mật khẩu tạm thời:</p>"
                + "<p><a href=\"" + resetLink + "\">Đặt lại mật khẩu</a></p>"
                + "<p>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này hoặc liên hệ với bộ phận hỗ trợ của chúng tôi.</p>"
                + "<p>Cảm ơn bạn đã chú ý.</p>"
                + "<p>Trân trọng,<br> Endless</p>";

        sendHtmlMail(to, subject, htmlBody);
    }

    public void sendTemporaryPasswordMail(String username, String to, String temporaryPassword) throws MessagingException {
        String subject = "Mật Khẩu Tạm Thời Của Bạn";
        String htmlBody = "<p>Chào " + username + ",</p>"
                + "<p>Như yêu cầu, chúng tôi đã tạo mật khẩu tạm thời cho tài khoản của bạn:</p>"
                + "<p><strong>" + temporaryPassword + "</strong></p>"
                + "<p>Vui lòng sử dụng mật khẩu này để đăng nhập và thay đổi mật khẩu ngay lập tức.</p>"
                + "<p>Nếu bạn không yêu cầu mật khẩu tạm thời này, vui lòng liên hệ với bộ phận hỗ trợ của chúng tôi ngay lập tức.</p>"
                + "<p>Cảm ơn bạn đã chú ý.</p>"
                + "<p>Trân trọng,<br> Endless</p>";

        sendHtmlMail(to, subject, htmlBody);
    }

    public void sendVerificationUpdateMail(String username, String to, String verificationLink) throws MessagingException {
        String subject = "Xác Minh Thay Đổi Email";
        String htmlBody = "<p>Chào " + username + ",</p>"
                + "<p>Cảm ơn bạn đã sử dụng dịch vụ của Endless.</p>"
                + "<p>Vui lòng nhấp vào liên kết dưới đây để xác minh địa chỉ email của bạn:</p>"
                + "<p><a href=\"" + verificationLink + "\">Xác nhận thay đổi email của bạn</a></p>"
                + "<p>Nếu bạn không yêu cầu thay đổi email, vui lòng bỏ qua email này.</p>"
                + "<p>Cảm ơn bạn đã chú ý.</p>"
                + "<p>Trân trọng,<br> Endless</p>";

        sendHtmlMail(to, subject, htmlBody);
    }

    public void sendVerificationMail(String username, String to, String verificationLink) throws MessagingException {
        String subject = "Xác Minh Email Tài Khoản Endless";
        String htmlBody = "<p>Chào " + username + ",</p>"
                + "<p>Cảm ơn bạn đã đăng ký với Endless.</p>"
                + "<p>Vui lòng nhấp vào liên kết dưới đây để xác minh địa chỉ email của bạn:</p>"
                + "<p><a href=\"" + verificationLink + "\">Xác Minh Email</a></p>"
                + "<p>Nếu bạn không tạo tài khoản này, vui lòng bỏ qua email này.</p>"
                + "<p>Cảm ơn bạn đã chú ý.</p>"
                + "<p>Trân trọng,<br> Endless</p>";

        sendHtmlMail(to, subject, htmlBody);
    }

    public void sendResetPasswordMail(String username, String to, String resetLink) throws MessagingException {
        String subject = "Yêu Cầu Đặt Lại Mật Khẩu";
        String htmlBody = "<p>Chào " + username + ",</p>"
                + "<p>Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn.</p>"
                + "<p>Nếu bạn đã yêu cầu, vui lòng nhấp vào liên kết dưới đây để đặt lại mật khẩu:</p>"
                + "<p><a href=\"" + resetLink + "\">Đặt lại mật khẩu</a></p>"
                + "<p>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này hoặc liên hệ với bộ phận hỗ trợ của chúng tôi.</p>"
                + "<p>Cảm ơn bạn đã chú ý.</p>"
                + "<p>Trân trọng,<br> Endless</p>";

        sendHtmlMail(to, subject, htmlBody);
    }



}
