package finalLab.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import finalLab.Model.Movie;


public class TicketService {

    private static final String TICKETS_FILE = "tickets/tickets.txt";


    public boolean isSeatAvailable(Movie movie, String date, String schedule, String seat) {
    Set<String> bookedSeats = getBookedSeats(movie, date, schedule);
    System.out.println("Booked seats for " + movie.getTitle() + " on " + date + " at " + schedule + ": " + bookedSeats);
    return !bookedSeats.contains(seat);
}



    public Set<String> getBookedSeats(Movie movie, String date, String schedule) {
        Set<String> bookedSeats = new HashSet<>();
        File file = new File(TICKETS_FILE);

        if (!file.exists() || !file.canRead()) {
            // No existing tickets, all seats are available.
            return bookedSeats;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder ticketBlock = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                ticketBlock.append(line).append("\n");

                if (line.trim().equals("|==================================|")) {
                    // End of ticket block: parse and check
                    String ticketText = ticketBlock.toString();

                    if (ticketMatches(movie, date, schedule, ticketText)) {
                        // Extract seats booked in this ticket
                        bookedSeats.addAll(parseSeatsFromTicket(ticketText));
                    }

                    ticketBlock.setLength(0); // reset for next ticket
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            // In case of error, conservatively assume no seats booked
        }

        return bookedSeats;
    }


    private boolean ticketMatches(Movie movie, String date, String schedule, String ticketText) {
    String movieTitle = extractField(ticketText, "Movie", 0);
    String screeningDate = extractField(ticketText, "Date", 1); // second date is the actual show date
    String screeningTime = extractField(ticketText, "Schedule", 0);

    boolean matched = movie.getTitle().equalsIgnoreCase(movieTitle) &&
                      date.equals(screeningDate) &&
                      schedule.equals(screeningTime);

    System.out.println("Matching ticket:");
    System.out.println(" - movie: " + movieTitle);
    System.out.println(" - date: " + screeningDate);
    System.out.println(" - schedule: " + screeningTime);
    System.out.println("MATCHED: " + matched);

    return matched;
}
    private String extractField(String ticketText, String fieldName) {
    return extractField(ticketText, fieldName, 0);
}

private String extractField(String ticketText, String fieldName, int occurrence) {
    int count = 0;
    for (String line : ticketText.split("\n")) {
        if (line.trim().startsWith(fieldName + "           :")) {
            if (count == occurrence) {
                return line.split(":", 2)[1].trim();
            }
            count++;
        }
    }
    return null;
}

    private Set<String> parseSeatsFromTicket(String ticketText) {
    Set<String> seats = new HashSet<>();
    String[] lines = ticketText.split("\n");
    for (String line : lines) {
        line = line.trim();
        if (line.startsWith("Seat           :")) {
            String seatPart = line.substring("Seat           :".length()).trim();
            if (seatPart.startsWith("[") && seatPart.endsWith("]")) {
                // Multiple seats case: [D5, D6]
                seatPart = seatPart.substring(1, seatPart.length() - 1); // remove brackets
                String[] seatArray = seatPart.split(",");
                for (String s : seatArray) {
                    seats.add(s.trim().toUpperCase());
                }
            } else if (!seatPart.isEmpty()) {
                
                seats.add(seatPart);
            }
            break; 
        }
    }
   return seats;
}

}

