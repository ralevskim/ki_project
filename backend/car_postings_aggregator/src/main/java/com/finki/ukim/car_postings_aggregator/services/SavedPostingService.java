package com.finki.ukim.car_postings_aggregator.services;

import com.finki.ukim.car_postings_aggregator.models.SavedPosting;
import com.finki.ukim.car_postings_aggregator.repositories.SavedPostingsJpaRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
public class SavedPostingService {

    private final String pazar3Url = "https://www.pazar3.mk";
    private final String reklama5Url = "https://www.reklama5.mk";
    private final SavedPostingsJpaRepository savedPostingsJpaRepository;
    private final CrawlCarPostingsService crawlCarPostingsService;

    public SavedPostingService(SavedPostingsJpaRepository savedPostingsJpaRepository,
                               CrawlCarPostingsService crawlCarPostingsService) {
        this.savedPostingsJpaRepository = savedPostingsJpaRepository;
        this.crawlCarPostingsService = crawlCarPostingsService;
    }

    private Optional<SavedPosting> findSavedPostingByUrl(String url) {
        return this.savedPostingsJpaRepository.findByPostingUrl(url);
    }

    public SavedPosting createSavedPosting(String url, User user) throws IOException {
        String searchUrl = "";
        if(!url.startsWith("http")) {
            if (url.contains("AdDetails"))
                searchUrl = this.reklama5Url + url;
            else
                searchUrl = this.pazar3Url + url;
        }
        else
            searchUrl = url;

        Optional<SavedPosting> savedPosting = this.findSavedPostingByUrl(searchUrl);
        if (savedPosting.isPresent())
            return savedPosting.get();

        SavedPosting newSavedPosting = this.crawlCarPostingsService.generateNewSavedPosting(url, user);
        this.savedPostingsJpaRepository.save(newSavedPosting);

        return newSavedPosting;
    }
}
