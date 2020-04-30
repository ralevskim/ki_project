package com.finki.ukim.car_postings_aggregator.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.finki.ukim.car_postings_aggregator.models.transferables.RequestRegistrationUser;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "user", schema = "test_schema")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @JsonIgnore
    private String password;

    @Column(unique = true)
    private String email;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(name = "saved_postings_users",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "posting_id")})
    private Set<SavedPosting> savedPostings = new HashSet<>();

    public User() {
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public User(RequestRegistrationUser requestRegistrationUser) {
        this.username = requestRegistrationUser.getUsername();
        this.email = requestRegistrationUser.getEmail();
        this.password = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public Set<SavedPosting> getSavedPostings() {
        return savedPostings;
    }

    public void setSavedPostings(Set<SavedPosting> savedPostings) {
        this.savedPostings = savedPostings;
    }
}

