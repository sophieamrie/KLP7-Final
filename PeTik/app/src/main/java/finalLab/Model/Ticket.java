package finalLab.Model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class Ticket {
    private String ticketId;
    private String customerName;
    private String movieTitle;
    private LocalDate showDate;
    private String showTime;
    private List<String> seats;
    private Map<String, Integer> snacks;
    private int totalAmount;

    public Ticket(String ticketId, String customerName, String movieTitle, LocalDate showDate,
            String showTime, List<String> seats, Map<String, Integer> snacks, int totalAmount) {
        this.ticketId = ticketId;
        this.customerName = customerName;
        this.movieTitle = movieTitle;
        this.showDate = showDate;
        this.showTime = showTime;
        this.seats = seats;
        this.snacks = snacks;
        this.totalAmount = totalAmount;
    }

    public String getTicketId() {
        return ticketId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public LocalDate getShowDate() {
        return showDate;
    }

    public String getShowTime() {
        return showTime;
    }

    public List<String> getSeats() {
        return seats;
    }

    public Map<String, Integer> getSnacks() {
        return snacks;
    }

    public int getTotalAmount() {
        return totalAmount;
    }
}