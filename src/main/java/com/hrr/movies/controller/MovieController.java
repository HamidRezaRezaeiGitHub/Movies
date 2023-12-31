package com.hrr.movies.controller;

import com.hrr.movies.model.Movie;
import com.hrr.movies.service.MovieService;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000/", maxAge = 3600)
@RequestMapping("/api/v1/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public ResponseEntity<List<Movie>> getAllMovies() {
        return new ResponseEntity<>(movieService.getAllMovies(), HttpStatus.OK);
    }

    @GetMapping("/internalId/{id}")
    public ResponseEntity<Movie> getMovieByInternalId(@PathVariable ObjectId id) {
        Optional<Movie> movieOptional = movieService.getMovieByInternalId(id);
        return movieOptional
                .map(movie -> new ResponseEntity<>(movie, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{imdbId}")
    public ResponseEntity<Movie> getMovieById(@PathVariable String imdbId) {
        Optional<Movie> movieOptional = movieService.getMovieByImdbId(imdbId);
        return movieOptional
                .map(movie -> new ResponseEntity<>(movie, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/deleteReviews/{imdbId}")
    public ResponseEntity<Void> deleteAllReviewsById(@PathVariable String imdbId) {
        movieService.deleteAllReviews(imdbId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
