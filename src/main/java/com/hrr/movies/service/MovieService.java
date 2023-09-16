package com.hrr.movies.service;

import com.hrr.movies.model.Movie;
import com.hrr.movies.repository.MovieRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<Movie> allMovies() {
        return movieRepository.findAll();
    }

    public Optional<Movie> movieByInternalId(ObjectId id) {
        return movieRepository.findById(id);
    }

    public Optional<Movie> movieById(String imdbId) {
        return movieRepository.findMovieByImdbId(imdbId);
    }

}
