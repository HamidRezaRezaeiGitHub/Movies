package com.hrr.movies.service;

import com.hrr.movies.model.Movie;
import com.hrr.movies.repository.MovieRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final ReviewService reviewService;

    public MovieService(MovieRepository movieRepository, ReviewService reviewService) {
        this.movieRepository = movieRepository;
        this.reviewService = reviewService;
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Optional<Movie> getMovieByInternalId(ObjectId id) {
        return movieRepository.findById(id);
    }

    public Optional<Movie> getMovieByImdbId(String imdbId) {
        return movieRepository.findMovieByImdbId(imdbId);
    }

    // This method does NOT change/update the reviews
    public Optional<Movie> updateMovie(Movie movie) {
        Optional<Movie> movieOptional = getMovieByImdbId(movie.getImdbId());
        return movieOptional.map(existingMovie -> {
            movie.setReviewIds(existingMovie.getReviewIds());
            return movieRepository.save(movie);
        });
    }

    public void deleteAllReviews(String imdbId) {
        Optional<Movie> movieOptional = getMovieByImdbId(imdbId);
        movieOptional.ifPresent(movie -> {
            movie.getReviewIds().forEach(review -> reviewService.deleteById(review.getId()));
            movie.setReviewIds(new ArrayList<>());
            movieRepository.save(movie);
        });
    }

}
