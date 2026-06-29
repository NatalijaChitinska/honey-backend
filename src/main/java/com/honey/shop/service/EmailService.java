package com.honey.shop.service;

import com.honey.shop.domain.Order;
import com.honey.shop.domain.OrderCreatedEvent;
import com.honey.shop.domain.enumerations.EmailTemplate;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.thymeleaf.context.Context;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final String sender;
    private static final String EMAIL_REGEX =
            "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public EmailService(JavaMailSender javaMailSender,
                        @Value("${spring.mail.username}") String sender) {
        this.javaMailSender = javaMailSender;
        this.sender = sender;
    }

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

    @Async
    @EventListener
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        Order order = event.getOrder();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender); // Matches your application.yml username
        message.setTo("natalijachitinska@gmail.com"); // The admin inbox
        message.setSubject("New Order Received! #" + order.getId());

        String emailBody = String.format(
                "Hello Admin,\n\nA new order has been placed in the system.\n\n" +
                        "Order ID: %d\n" +
                        "Customer: %s\n" +
                        "Items: %s\n\n" +
                        "Please check the admin panel to process this request.",
                order.getId(), order.getCustomerName(),
                order.getOrderItems().stream()
                        .map(item -> item.getProduct().getName() + " x " + item.getQuantity())
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("No items")
        );

        message.setText(emailBody);

        // Sends the mail over port 1025 to Mailpit
        javaMailSender.send(message);
    }

    private String loadTemplate(String fileName) throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/" + fileName);
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }
}
