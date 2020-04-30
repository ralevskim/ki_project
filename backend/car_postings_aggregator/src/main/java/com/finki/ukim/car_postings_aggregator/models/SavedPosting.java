package com.finki.ukim.car_postings_aggregator.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.finki.ukim.car_postings_aggregator.models.transferables.PostingDetails;
import org.hibernate.validator.constraints.URL;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "saved_postings", schema = "test_schema")
public class SavedPosting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @URL
    private String postingUrl;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "savedPostings")
    private Set<User> users = new HashSet<>();

    private String postingImageUrl;

    private String title;

    private String vehicleBrand;

    private String vehicleModel;

    public SavedPosting() {
    }

    public SavedPosting(PostingDetails postingDetails, String postingUrl) {
        this.postingUrl = postingUrl;
        this.postingImageUrl = postingDetails.getPostingImageUrls().size() != 0
                ? postingDetails.getPostingImageUrls().get(0)
                : "Нема слика";
        if (this.postingImageUrl.contains("reklama5"))
            this.postingImageUrl = "http://" + this.postingImageUrl;

        this.title = postingDetails.getTitle();
        this.vehicleBrand = postingDetails.getVehicleBrand();
        this.vehicleModel = postingDetails.getVehicleModel();
    }

    public SavedPosting(String postingUrl, String postingImageUrl, String title, String vehicleBrand, String vehicleModel) {
        this.postingUrl = postingUrl;
        this.postingImageUrl = postingImageUrl;
        this.title = title;
        this.vehicleBrand = vehicleBrand;
        this.vehicleModel = vehicleModel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPostingUrl() {
        return postingUrl;
    }

    public void setPostingUrl(String postingUrl) {
        this.postingUrl = postingUrl;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SavedPosting that = (SavedPosting) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(postingUrl, that.postingUrl) &&
                Objects.equals(users, that.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, postingUrl, users);
    }

    public String getPostingImageUrl() {
        return postingImageUrl;
    }

    public void setPostingImageUrl(String postingImageUrl) {
        this.postingImageUrl = postingImageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVehicleBrand() {
        return vehicleBrand;
    }

    public void setVehicleBrand(String vehicleBrand) {
        this.vehicleBrand = vehicleBrand;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }
}
