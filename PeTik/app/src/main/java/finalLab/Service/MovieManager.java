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
        movies.add(new Movie("The Batman", "Action/Crime", 50000));
        movies.add(new Movie("20th Century Girl", "Romance/Comedy", 50000));
        movies.add(new Movie("Pengepungan di Bukit Duri", "Action", 45000));
        movies.add(new Movie("Pengabdi Setan 2", "Horror", 50000));
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