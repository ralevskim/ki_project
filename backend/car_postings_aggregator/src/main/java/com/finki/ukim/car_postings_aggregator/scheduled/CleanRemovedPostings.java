package com.finki.ukim.car_postings_aggregator.scheduled;

import com.finki.ukim.car_postings_aggregator.models.SavedPosting;
import com.finki.ukim.car_postings_aggregator.repositories.SavedPostingsJpaRepository;
import com.finki.ukim.car_postings_aggregator.services.MailSendingService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

@Component
public class CleanRemovedPostings {

    private final SavedPostingsJpaRepository savedPostingsJpaRepository;
    private final MailSendingService mailSendingService;

    public CleanRemovedPostings(SavedPostingsJpaRepository savedPostingsJpaRepository,
                                MailSendingService mailSendingService) {
        this.savedPostingsJpaRepository = savedPostingsJpaRepository;
        this.mailSendingService = mailSendingService;
    }

    // check for removed postings each night at 3 AM
    @Scheduled(cron = "0 0 3 * * ?")
    public void deleteRemovedPostings() {
        List<SavedPosting> savedPostings = this.savedPostingsJpaRepository.findAll();

        savedPostings.forEach(savedPosting -> {
            try {
                URL url = new URL(savedPosting.getPostingUrl());
                URLConnection urlConnection = url.openConnection();
                urlConnection.addRequestProperty("User-Agent", "Chrome");
            } catch (MalformedURLException ignored) {
                // if the URL doesn't exist - notify all the users that follow this posting
            } catch (IOException ioException) {
                savedPosting.getUsers().forEach(user -> {
                    this.mailSendingService.createAndSendEmail(user.getEmail(), user.getUsername(), savedPosting.getPostingUrl());
                });
            }
        });

    }
}
