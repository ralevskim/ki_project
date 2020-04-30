package com.finki.ukim.car_postings_aggregator.services;

import com.finki.ukim.car_postings_aggregator.models.SavedPosting;
import com.finki.ukim.car_postings_aggregator.models.transferables.PostingBasic;
import com.finki.ukim.car_postings_aggregator.models.transferables.PostingDetails;
import com.finki.ukim.car_postings_aggregator.models.transferables.PostingFilterParameters;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Service
public class CrawlCarPostingsService {

    private final CrawlPazar3Service crawlPazar3Service;
    private final CrawlReklama5Service crawlReklama5Service;
    private final String reklama5Url = "https://www.reklama5.mk";
    private final String pazar3Url = "https://www.pazar3.mk";
    private final String baseUrlP3 = "/oglasi/vozila/avtomobili";
    private final String baseUrlR5 = "/Search?q=&city=267&sell=0&sell=1&buy=0&buy=1&trade=0&trade=1&includeOld=0&includeOld=1&includeNew=0&includeNew=1&private=0&company=0&page=1&SortByPrice=0&zz=1&cat=24";

    public CrawlCarPostingsService(CrawlPazar3Service crawlPazar3Service, CrawlReklama5Service crawlReklama5Service) {
        this.crawlPazar3Service = crawlPazar3Service;
        this.crawlReklama5Service = crawlReklama5Service;
    }

    public Set<PostingBasic> getPostingsFromSourcesNoFilters() throws IOException {
        ArrayList<PostingBasic> postingsP3 = (ArrayList<PostingBasic>) this.crawlPazar3Service.getPostingsForP3Url(this.baseUrlP3, false);
        ArrayList<PostingBasic> postingsR5 = (ArrayList<PostingBasic>) this.crawlReklama5Service.getPostingsForR5Url(this.baseUrlR5);

        HashSet<PostingBasic> crawledBasicPostings = new HashSet<>();
        crawledBasicPostings.addAll(postingsP3);
        crawledBasicPostings.addAll(postingsR5);

        return crawledBasicPostings;
    }

    public Set<PostingBasic> getPostingsFromSourcesNoFiltersPaged(int pageNumber) throws IOException {
        ArrayList<PostingBasic> postingsP3 = (ArrayList<PostingBasic>) this.crawlPazar3Service.getNextPagePostingsP3Url(this.baseUrlP3, pageNumber);
        ArrayList<PostingBasic> postingsR5 = (ArrayList<PostingBasic>) this.crawlReklama5Service.getNextPagePostingsR5Url(this.baseUrlR5, pageNumber);

        HashSet<PostingBasic> crawledBasicPostings = new HashSet<>();
        crawledBasicPostings.addAll(postingsP3);
        crawledBasicPostings.addAll(postingsR5);

        return crawledBasicPostings;
    }

    public SavedPosting generateNewSavedPosting(String url, User user) throws IOException {
        if(url.contains("AdDetails"))
            return new SavedPosting(this.crawlReklama5Service.getDetailedPostingForR5Url(url, user), this.reklama5Url+url);
        else {
            return new SavedPosting(this.crawlPazar3Service.getDetailedPostingForP3Url(url, user), this.pazar3Url+url);
        }
    }

    public PostingDetails getDetailedPostingForUrl(String url, User user) throws IOException {
        if (!url.contains("AdDetails"))
            return this.crawlPazar3Service.getDetailedPostingForP3Url(url, user);
        else
            return this.crawlReklama5Service.getDetailedPostingForR5Url(url, user);
    }

    public Set<PostingBasic> searchPostingsByPhrase(String searchPhrase) throws IOException {
        ArrayList<PostingBasic> postingsP3 = (ArrayList<PostingBasic>) this.crawlPazar3Service.searchPostingsP3(searchPhrase);
        ArrayList<PostingBasic> postingsR5 = (ArrayList<PostingBasic>) this.crawlReklama5Service.searchPostingsR5(searchPhrase);

        HashSet<PostingBasic> crawledBasicPostings = new HashSet<>();
        crawledBasicPostings.addAll(postingsP3);
        crawledBasicPostings.addAll(postingsR5);

        return crawledBasicPostings;
    }

    public Set<PostingBasic> getConditionalPostings(PostingFilterParameters postingFilterParameters) throws IOException {
        ArrayList<PostingBasic> postingsP3 = (ArrayList<PostingBasic>) this.crawlPazar3Service.getConditionalPostingsP3(postingFilterParameters);
        ArrayList<PostingBasic> postingsR5 = (ArrayList<PostingBasic>) this.crawlReklama5Service.getConditionalPostingsR5(postingFilterParameters);

        HashSet<PostingBasic> crawledBasicPostings = new HashSet<>();
        crawledBasicPostings.addAll(postingsP3);
        crawledBasicPostings.addAll(postingsR5);

        return crawledBasicPostings;
    }

    public Set<PostingBasic> getConditionalPostingsPaged(PostingFilterParameters postingFilterParameters, int pageNumber) throws IOException {
        String conditionalUrlP3 = this.crawlPazar3Service.generateConditionalUrlP3(postingFilterParameters);
        String conditionalUrlR5 = this.crawlReklama5Service.generateConditionalUrlR5(postingFilterParameters);

        ArrayList<PostingBasic> postingsP3 = (ArrayList<PostingBasic>) this.crawlPazar3Service.getNextPagePostingsP3Url(conditionalUrlP3, pageNumber);
        ArrayList<PostingBasic> postingsR5 = (ArrayList<PostingBasic>) this.crawlReklama5Service.getNextPagePostingsR5Url(conditionalUrlR5, pageNumber);

        HashSet<PostingBasic> crawledBasicPostings = new HashSet<>();
        crawledBasicPostings.addAll(postingsP3);
        crawledBasicPostings.addAll(postingsR5);

        return crawledBasicPostings;
    }

}
