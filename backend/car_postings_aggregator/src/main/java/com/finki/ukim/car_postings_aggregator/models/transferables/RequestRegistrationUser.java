package com.finki.ukim.car_postings_aggregator.models.transferables;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;

public class RequestRegistrationUser {

    @Min(value = 5, message = "Корисничкото име е премногу кратко. Ве молиме користете барем 5 карактери.")
    private String username;

    @Email
    private String email;

    @Min(value = 6, message = "Вашиот пасворд мора да содржи барем 6 карактери.")
    private String password;

    public RequestRegistrationUser(@Min(value = 5, message = "Корисничкото име е премногу кратко. Ве молиме користете барем 5 карактери.") String username,
                                   @Email String email,
                                   @Min(value = 6, message = "Вашиот пасворд мора да содржи барем 6 карактери.") String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}


