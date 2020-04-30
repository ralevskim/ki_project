package com.finki.ukim.car_postings_aggregator.rest;

import com.finki.ukim.car_postings_aggregator.models.transferables.PostingBasic;
import com.finki.ukim.car_postings_aggregator.models.transferables.PostingDetails;
import com.finki.ukim.car_postings_aggregator.models.transferables.PostingFilterParameters;
import com.finki.ukim.car_postings_aggregator.services.CrawlCarPostingsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Set;

@RestController
@RequestMapping("/api/car-postings")
@CrossOrigin(origins = "http://localhost:4200")
public class CarPostingsController {
    private final CrawlCarPostingsService crawlCarPostingsService;

    public CarPostingsController(CrawlCarPostingsService crawlCarPostingsService) {
        this.crawlCarPostingsService = crawlCarPostingsService;
    }

    @GetMapping
    public Set<PostingBasic> getNewestCrawledPostings() throws IOException {
        return this.crawlCarPostingsService.getPostingsFromSourcesNoFilters();
    }

    @GetMapping("/page/{page-num}")
    public Set<PostingBasic> getCrawledPostingsPaged(@PathVariable("page-num") int pageNumber) throws IOException {
        return this.crawlCarPostingsService.getPostingsFromSourcesNoFiltersPaged(pageNumber);
    }

    @PostMapping("/with-filters")
    public Set<PostingBasic> getCrawledPostingsWithFilters(@RequestBody PostingFilterParameters postingFilterParameters)
            throws IOException {
        return this.crawlCarPostingsService.getConditionalPostings(postingFilterParameters);
    }

    @PostMapping("/with-filters/page/{page-num}")
    public Set<PostingBasic> getCrawledPostingsWithFilters(@RequestBody PostingFilterParameters postingFilterParameters,
                                                           @PathVariable("page-num") int pageNumber)
            throws IOException {
        return this.crawlCarPostingsService.getConditionalPostingsPaged(postingFilterParameters, pageNumber);
    }

    @GetMapping("/search")
    public Set<PostingBasic> searchPostingsByPhrase(@RequestParam("query") String searchPhrase) throws IOException {
        return this.crawlCarPostingsService.searchPostingsByPhrase(searchPhrase);
    }

    @PostMapping("/details")
    public PostingDetails getPostingDetails(@RequestBody String detailsUrl, @ModelAttribute User user) throws IOException {
        return this.crawlCarPostingsService.getDetailedPostingForUrl(detailsUrl, user);
    }

}
