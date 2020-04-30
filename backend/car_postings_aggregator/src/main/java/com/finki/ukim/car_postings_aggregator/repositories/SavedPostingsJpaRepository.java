package com.finki.ukim.car_postings_aggregator.repositories;

import com.finki.ukim.car_postings_aggregator.models.SavedPosting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SavedPostingsJpaRepository extends JpaRepository<SavedPosting, Long> {
    Optional<SavedPosting> findByPostingUrl(String postingUrl);
}
