package application_operation.ParkFlow.config;

import application_operation.ParkFlow.dto.mail.EmailDto;
import application_operation.ParkFlow.enums.ResponseCodeEnum;
import application_operation.ParkFlow.exception.HandleException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailConfig {
    private final JavaMailSender mailSender;

    @Async
    public void consumeEmail(EmailDto emailDto) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailDto.getEmail());
            message.setSubject(emailDto.getSubject());
            message.setText(emailDto.getText());
            message.setFrom("${spring.mail.username}");

            mailSender.send(message);
        } catch (Exception e) {
            throw new HandleException(ResponseCodeEnum.MAIL_ERROR.getResponseCode(), e.getMessage());
        }
    }
}
