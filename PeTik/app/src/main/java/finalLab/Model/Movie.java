package finalLab.Model;

public class Movie {
    private String title;
    private String genre;
    private int price;

    public Movie(String title, String genre, int price) {
        this.title = title;
        this.genre = genre;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public int getPrice() {
        return price;
    }
}