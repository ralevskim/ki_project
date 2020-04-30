package com.finki.ukim.car_postings_aggregator.models.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidOldPasswordException extends Exception {
    public InvalidOldPasswordException(String s) {
    }
}
