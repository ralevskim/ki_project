package com.finki.ukim.car_postings_aggregator.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailSendingService {

    private final JavaMailSender javaMailSender;

    private final String message = "Драг USER,\nЗа жал вашиот оглас со следниов линк:\n\t\tURL\nповеќе не постои затоа" +
            "што беше избришан од соодветниот веб сајт.\nИмајте пријатен ден,\nВашиот агрегатор на автомобилски огласи.";

    private final String companyEmail = "your_car_aggregator@caraggregator.com";

    public MailSendingService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void createAndSendEmail(String userEmail, String username, String postingUrl){
        String content = this.message.replace("USER", username);
        content = content.replace("URL", postingUrl);

        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(this.companyEmail);
        email.setTo(userEmail);
        email.setSubject("Избришан оглас - Вашиот агрегатор на автомобилски огласи");
        email.setText(content);

        this.javaMailSender.send(email);
    }
}
