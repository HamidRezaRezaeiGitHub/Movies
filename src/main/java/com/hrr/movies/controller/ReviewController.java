package com.hrr.movies.controller;

import com.hrr.movies.model.Review;
import com.hrr.movies.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/{imdbId}")
    public ResponseEntity<Review> createReviewForImdbId(@PathVariable String imdbId, @RequestBody Map<String, String> payload) {
        Optional<Review> reviewOptional = reviewService.createReview(payload.get("reviewBody"), imdbId);
        return reviewOptional
                .map(review -> new ResponseEntity<>(review, HttpStatus.CREATED))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{imdbId}")
    public ResponseEntity<List<Review>> getAllReviewsOfImdbId(@PathVariable String imdbId) {
        Optional<List<Review>> reviewsOptional = reviewService.getAllReviews(imdbId);
        return reviewsOptional
                .map(reviews -> new ResponseEntity<>(reviews, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
