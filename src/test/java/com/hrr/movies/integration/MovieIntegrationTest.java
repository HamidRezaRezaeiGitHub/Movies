package com.hrr.movies.integration;

import com.hrr.movies.controller.MovieController;
import com.hrr.movies.controller.ReviewController;
import com.hrr.movies.model.Movie;
import com.hrr.movies.repository.MovieRepository;
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
public class MovieIntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(MovieIntegrationTest.class);

    @Autowired
    private MovieController movieController;
    @Autowired
    private ReviewController reviewController;
    @Autowired
    private MovieRepository movieRepository;
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
    public void test_okCorrectList_when_getAllMovies() {
        ResponseEntity<List<Movie>> response = movieController.getAllMovies();
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Optional<Movie> movieOptional = response.getBody().stream().filter(movie1 -> movie1.getImdbId().equals("test imdb")).findFirst();
        Assertions.assertTrue(movieOptional.isPresent());
        Assertions.assertEquals(movie, movieOptional.get());
    }

    @Test
    public void test_okCorrectMovie_when_getMovieById() {
        ResponseEntity<Movie> response = movieController.getMovieById("test imdb");
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(movie, response.getBody());
    }

    @Test
    public void test_notFoundNull_when_getMovieById() {
        ResponseEntity<Movie> response = movieController.getMovieById("imdb2");
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertNull(response.getBody());
    }

    @Test
    public void test_ok_when_deleteAllReviewsById() {
        ResponseEntity<Void> response = movieController.deleteAllReviewsById("test imdb");
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void test_reviewsDeleted_when_deleteAllReviewsById() {
        Map<String, String> payload = new HashMap<>();
        payload.put("reviewBody", "test review body");
        reviewController.createReviewForImdbId("test imdb", payload);

        ResponseEntity<Movie> response = movieController.getMovieById("test imdb");
        Assertions.assertNotNull(response.getBody());
        Movie retrievedMovie = response.getBody();
        Assertions.assertTrue(retrievedMovie.getReviewIds() != null && retrievedMovie.getReviewIds().size() == 1);

        movieController.deleteAllReviewsById("test imdb");
        response = movieController.getMovieById("test imdb");
        Assertions.assertNotNull(response.getBody());
        retrievedMovie = response.getBody();
        Assertions.assertTrue(retrievedMovie.getReviewIds() == null || retrievedMovie.getReviewIds().isEmpty());
    }
}
