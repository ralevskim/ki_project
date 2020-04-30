package com.finki.ukim.car_postings_aggregator.models.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AlreadyExistingEmail extends Exception{

    public AlreadyExistingEmail(String msg){
        super(msg);
    }

}
