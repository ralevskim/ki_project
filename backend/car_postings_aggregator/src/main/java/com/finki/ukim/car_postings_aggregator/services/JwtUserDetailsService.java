package com.finki.ukim.car_postings_aggregator.services;

import com.finki.ukim.car_postings_aggregator.models.User;
import com.finki.ukim.car_postings_aggregator.repositories.UsersJpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final UsersJpaRepository userJpaRepository;

    public JwtUserDetailsService(UsersJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String loginCredentials) throws UsernameNotFoundException {
        User user = null;
        user = this.getUserByLoginCredentials(loginCredentials);

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                new ArrayList<>()
        );
    }

    public User getUserByLoginCredentials(String loginCredentials) {
        return this.userJpaRepository.findByUsernameOrEmail(loginCredentials, loginCredentials)
                .orElseThrow(() -> new UsernameNotFoundException("User not found for " + loginCredentials + "."));

    }
}
