package com.finki.ukim.car_postings_aggregator.models.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends Exception {

    public UserNotFoundException(String msg) {
        super(msg);
    }

}
