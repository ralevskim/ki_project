package com.finki.ukim.car_postings_aggregator.configurations.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class SecurityControllerAdvice {

    @ModelAttribute
    public User customPrincipal(Authentication a) {
        return a == null ? null : (User) a.getPrincipal();
    }

}
