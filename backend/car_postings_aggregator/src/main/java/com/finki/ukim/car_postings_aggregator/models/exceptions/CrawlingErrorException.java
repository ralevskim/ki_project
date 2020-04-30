package com.finki.ukim.car_postings_aggregator.models.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class CrawlingErrorException extends Exception {

    public CrawlingErrorException(String message) {
        super(message);
    }

}
