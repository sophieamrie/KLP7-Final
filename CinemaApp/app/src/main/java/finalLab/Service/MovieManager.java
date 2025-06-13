package finalLab.Service;

import finalLab.Model.Movie;
import java.util.ArrayList;
import java.util.List;

public class MovieManager {

    private List<Movie> movies;

    public MovieManager() {
        movies = new ArrayList<>();
        initializeMovies();
    }

    private void initializeMovies() {
        movies.add(new Movie("Avengers: Endgame", "Action/Adventure", 50000));
        movies.add(new Movie("Spider-Man: No Way Home", "Action/Adventure", 55000));
        movies.add(new Movie("The Batman", "Action/Crime", 50000));
        movies.add(new Movie("Top Gun: Maverick", "Action/Drama", 45000));
        movies.add(new Movie("Doctor Strange 2", "Action/Fantasy", 50000));
        movies.add(new Movie("Jurassic World: Dominion", "Action/Adventure", 45000));
    }

    public List<Movie> getAllMovies() {
        return new ArrayList<>(movies);
    }

    public Movie getMovieByTitle(String title) {
        return movies.stream()
                .filter(movie -> movie.getTitle().equals(title))
                .findFirst()
                .orElse(null);
    }
}