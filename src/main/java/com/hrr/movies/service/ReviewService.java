package com.hrr.movies.service;

import com.hrr.movies.model.Movie;
import com.hrr.movies.model.Review;
import com.hrr.movies.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final MovieService movieService;

    public ReviewService(ReviewRepository reviewRepository, MovieService movieService) {
        this.reviewRepository = reviewRepository;
        this.movieService = movieService;
    }

    public Optional<Review> createReview(String reviewBody, String imdbId) {
        Optional<Movie> movieOptional = movieService.getMovieByImdbId(imdbId);
        if (movieOptional.isPresent()) {
            Review review = reviewRepository.insert(new Review(reviewBody));
            movieOptional.get().addReview(review);
            movieService.updateMovie(movieOptional.get());
            return Optional.of(review);
        } else {
            return Optional.empty();
        }
    }

    public Optional<List<Review>> getAllReviews(String imdbId) {
        Optional<Movie> movieOptional = movieService.getMovieByImdbId(imdbId);
        return movieOptional.map(Movie::getReviewIds);
    }

}
