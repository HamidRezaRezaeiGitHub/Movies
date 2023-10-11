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
@CrossOrigin(origins = "http://localhost:3000/", maxAge = 3600)
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    public static final List<String> PAYLOAD_MANDATORY_FIELDS = List.of("reviewBody");
    public static final String MANDATORY_FIELD_MISSING_MESSAGE = "Mandatory fields are not passed: " + PAYLOAD_MANDATORY_FIELDS;

    public static boolean verifyPayloadMandatoryFields(Map<String, String> payload) {
        for (String payloadMandatoryField : PAYLOAD_MANDATORY_FIELDS) {
            if (!payload.containsKey(payloadMandatoryField)) return false;
        }
        return true;
    }

    @PostMapping("/{imdbId}")
    public ResponseEntity createReviewForImdbId(@PathVariable String imdbId, @RequestBody Map<String, String> payload) {
        if (!verifyPayloadMandatoryFields(payload)) {
            return new ResponseEntity<>(MANDATORY_FIELD_MISSING_MESSAGE, HttpStatus.BAD_REQUEST);
        }
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

    @DeleteMapping("/orphan")
    public ResponseEntity<Void> deleteOrphanReviews() {
        reviewService.deleteOrphans();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
