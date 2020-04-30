package com.finki.ukim.car_postings_aggregator.models.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AlreadyExistingUsername extends Exception{

    public AlreadyExistingUsername(String msg){
        super(msg);
    }

}
