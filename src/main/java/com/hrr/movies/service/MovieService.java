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

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Optional<Movie> getMovieByInternalId(ObjectId id) {
        return movieRepository.findById(id);
    }

    public Optional<Movie> getMovieByImdbId(String imdbId) {
        return movieRepository.findMovieByImdbId(imdbId);
    }

    public Optional<Movie> updateMovie(Movie movie) {
        Optional<Movie> movieOptional = getMovieByImdbId(movie.getImdbId());
        return movieOptional.map(movie1 -> movieRepository.save(movie));
    }

}
