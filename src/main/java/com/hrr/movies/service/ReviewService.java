package com.hrr.movies.service;

import com.hrr.movies.model.Movie;
import com.hrr.movies.model.Review;
import com.hrr.movies.repository.MovieRepository;
import com.hrr.movies.repository.ReviewRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final MovieRepository movieRepository;

    public ReviewService(ReviewRepository reviewRepository, MovieRepository movieRepository) {
        this.reviewRepository = reviewRepository;
        this.movieRepository = movieRepository;
    }

    public Optional<Review> createReview(String reviewBody, String imdbId) {
        Optional<Movie> movieOptional = movieRepository.findMovieByImdbId(imdbId);
        if (movieOptional.isPresent()) {
            Review review = reviewRepository.insert(new Review(reviewBody));
            Movie movie = movieOptional.get();
            movie.addReview(review);
            movieRepository.save(movie);
            return Optional.of(review);
        } else {
            return Optional.empty();
        }
    }

    public Optional<List<Review>> getAllReviews(String imdbId) {
        Optional<Movie> movieOptional = movieRepository.findMovieByImdbId(imdbId);
        return movieOptional.map(Movie::getReviewIds);
    }

    public void deleteById(ObjectId id) {
        reviewRepository.deleteById(id);
    }

}
