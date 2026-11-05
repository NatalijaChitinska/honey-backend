package com.honey.shop.service;

import com.honey.shop.domain.Order;
import com.honey.shop.domain.OrderCreatedEvent;
import com.honey.shop.domain.enumerations.EmailTemplate;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import org.thymeleaf.context.Context;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final String sender;
    private final String adminNotificationEmail;
    private static final String EMAIL_REGEX =
            "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public EmailService(JavaMailSender javaMailSender,
                        @Value("${spring.mail.username}") String sender,
                        @Value("${app.mail.admin-notification-email:}") String adminNotificationEmail) {
        this.javaMailSender = javaMailSender;
        this.sender = sender;
        this.adminNotificationEmail = adminNotificationEmail == null || adminNotificationEmail.isBlank()
                ? sender
                : adminNotificationEmail;
    }

//    @Async("mailTaskExecutor")
    public void sendHtmlEmail(
            Map<String, String> content, EmailTemplate emailTemplate, String email) {
        Thread thread =
                new Thread(
                        () -> {
                            Context context = new Context();
                            context.setVariable("content", content);

                            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
                            try {
                                String html = loadTemplate(emailTemplate.getHtmlTemplate());

                                for (Map.Entry<String, String> entry : content.entrySet()) {
                                    html = html.replace("{{" + entry.getKey() + "}}", entry.getValue());
                                }

                                helper.setFrom(sender);
                                helper.setSubject(emailTemplate.getSubject());
                                helper.setText(html, true);
                                helper.setTo(email);

                                javaMailSender.send(mimeMessage);
                            } catch (MessagingException | IOException e) {
                                e.printStackTrace();
                            }
                        });
        thread.start();
    }

    @EventListener
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        Order order = event.getOrder();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(adminNotificationEmail);
        message.setSubject("New Order Received! #" + order.getId());

        String emailBody = String.format(
                "Здраво Админ,\n\nНаправена е нова нарачка\n\n" +
                        "Order ID: %d\n" +
                        "Име: %s\n" +
                        "Тел: %s\n" +
                        "Производи: %s\n\n" +
                        "Ве молиме проверете го админ панелот за да ја обработите оваа нарачка.",
                order.getId(), order.getCustomerName(), order.getCustomerPhone(),
                order.getOrderItems().stream()
                        .map(item -> item.getProduct().getName() + " x " + item.getQuantity())
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("No items")
        );

        message.setText(emailBody);

        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            log.error("Failed to send admin order notification for order {}: {}", order.getId(), e.getMessage());
        }
    }

    private String loadTemplate(String fileName) throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/" + fileName);
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }
}
