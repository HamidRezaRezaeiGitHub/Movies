package com.hrr.movies.integration;

import com.hrr.movies.controller.ReviewController;
import com.hrr.movies.model.Movie;
import com.hrr.movies.model.Review;
import com.hrr.movies.repository.MovieRepository;
import com.hrr.movies.repository.ReviewRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

@SpringBootTest
public class ReviewIntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(ReviewIntegrationTest.class);

    @Autowired
    private ReviewController reviewController;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    private Movie movie;

    @BeforeEach
    public void setup() {
        movie = Movie.builder()
                .imdbId("test imdb")
                .title("test title")
                .releaseDate("test releaseDate")
                .trailerLink("test trailerLink")
                .poster("test poster")
                .genres(new ArrayList<>())
                .backdrops(new ArrayList<>())
                .reviewIds(new ArrayList<>())
                .build();
        movieRepository.save(movie);
        logger.info("A movie saved into the repository. {}", movieRepository.findAll());
        Optional<Movie> movieOptional = movieRepository.findMovieByImdbId("test imdb");
        Assertions.assertTrue(movieOptional.isPresent());
        logger.info("The saved movie: {}", movieOptional.get());
        Assertions.assertEquals("test imdb", movieOptional.get().getImdbId());
    }

    @AfterEach
    public void cleanup() {
        movieRepository.deleteByImdbId("test imdb");
        logger.info("The test movie deleted from the repository. {}", movieRepository.findAll());
    }

    @Test
    public void test_notFoundNull_when_getAllReviewsOfImdbId_wrongImdbId() {
        ResponseEntity<List<Review>> response = reviewController.getAllReviewsOfImdbId("wrong");
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertNull(response.getBody());
    }

    @Test
    public void test_okEmptyList_when_getAllReviewsOfImdbId_correctImdbId() {
        ResponseEntity<List<Review>> response = reviewController.getAllReviewsOfImdbId("test imdb");
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertTrue(response.getBody() == null || response.getBody().isEmpty());
    }

    // This test is meant to show that MongoRepository doesn't cascade save method.
    @Test
    public void test_okEmptyList_when_getAllReviewsOfImdbId_correctImdbId_notCascading() {
        Review review = new Review("test review body");
        movie.addReview(review);
//        logger.info("Saving new review in the repository: {}", reviewRepository.save(review));
        logger.info("Saving updated movie in the repository: {}", movieRepository.save(movie));
        ResponseEntity<List<Review>> response = reviewController.getAllReviewsOfImdbId("test imdb");
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertTrue(response.getBody() == null || response.getBody().isEmpty());
    }

    @Test
    public void test_okCorrectList_when_getAllReviewsOfImdbId_correctImdbId() {
        Review review = new Review("test review body");
        movie.addReview(review);
        logger.info("Saving new review in the repository: {}", reviewRepository.save(review));
        logger.info("Saving updated movie in the repository: {}", movieRepository.save(movie));
        ResponseEntity<List<Review>> response = reviewController.getAllReviewsOfImdbId("test imdb");
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertTrue(response.getBody() != null && !response.getBody().isEmpty());
    }

    @Test
    public void test_badRequest_when_createReviewForImdbId_badPayload() {
        ResponseEntity responseEntity = reviewController.createReviewForImdbId("test imdb", new HashMap<>());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void test_notFound_when_createReviewForImdbId_wrongImdbId() {
        Map<String, String> payload = new HashMap<>();
        payload.put("reviewBody", "test review body");
        ResponseEntity response = reviewController.createReviewForImdbId("wrong", payload);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void test_createdCorrectReview_when_createReviewForImdbId() {
        Map<String, String> payload = new HashMap<>();
        payload.put("reviewBody", "test review body");
        ResponseEntity response = reviewController.createReviewForImdbId("test imdb", payload);
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Review createdReview = (Review) response.getBody();
        Assertions.assertEquals("test review body", createdReview.getBody());
    }

    @Test
    public void test_correctReviewSaved_when_createReviewForImdbId() {
        Map<String, String> payload = new HashMap<>();
        payload.put("reviewBody", "test review body");
        reviewController.createReviewForImdbId("test imdb", payload);

        ResponseEntity<List<Review>> response = reviewController.getAllReviewsOfImdbId("test imdb");
        Assertions.assertTrue(response.getBody() != null && !response.getBody().isEmpty());
        Assertions.assertEquals("test review body", response.getBody().get(0).getBody());
    }

    @Test
    public void test_orphanReviewsDeleted_when_deleteOrphanReviews() {
        Review orphanReview = new Review("test review body");
        logger.info("Saving new orphan review in the repository: {}", reviewRepository.save(orphanReview));
        ResponseEntity<Void> response = reviewController.deleteOrphanReviews();
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertFalse(reviewRepository.findAll().contains(orphanReview));
    }
}
