package com.finki.ukim.car_postings_aggregator.models.transferables;

import java.io.Serializable;

public class JwtResponse implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;

    private final String jwtToken;

    private final String username;

    private final Long id;

    private final String email;

    public JwtResponse(String jwtToken, String username, Long id, String email) {
        this.jwtToken = jwtToken;
        this.username = username;
        this.id = id;
        this.email = email;
    }

    public String getToken() {
        return this.jwtToken;
    }

    public String getUsername() {
        return this.username;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
}
