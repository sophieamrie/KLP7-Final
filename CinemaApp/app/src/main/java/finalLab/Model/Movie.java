package finalLab.Model;

import java.util.List;

public class Movie {
    private String title;
    private String genre;
    private int duration;
    private List<String> schedules;
    private String posterPath;
    private String trailerPath;
    private String id;

    
    
    
    public Movie(String title, String genre, int duration, List<String> schedules, String posterPath) {
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.schedules = schedules;
        this.posterPath = posterPath;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<String> getSchedules() {
        return schedules;
    }
    
    public void setSchedules(List<String> schedules) {
        this.schedules = schedules;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }
    public String getTrailerPath() {
        return trailerPath;
    }

    public void setTrailerPath(String trailerPath) {
        this.trailerPath = trailerPath;
    }
    
    @Override
    public String toString() {
        return "Movie{" +
                "title='" + title + '\'' +
                ", genre='" + genre + '\'' +
                ", duration=" + duration +
                ", schedules=" + schedules +
                '}';
    }
}
