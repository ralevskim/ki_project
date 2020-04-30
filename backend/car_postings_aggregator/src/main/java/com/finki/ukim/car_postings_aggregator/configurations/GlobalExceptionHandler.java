package com.finki.ukim.car_postings_aggregator.configurations;

import com.finki.ukim.car_postings_aggregator.models.exceptions.CrawlingErrorException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IOException.class)
    public void handleException() throws CrawlingErrorException {
        throw new CrawlingErrorException("Настана грешка при агрегирање на огласите. Обидете се повторно подоцна.");
    }
}
