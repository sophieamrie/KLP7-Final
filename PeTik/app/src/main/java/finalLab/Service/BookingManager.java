package finalLab.Service;

import finalLab.Model.Ticket;
import java.time.LocalDate;
import java.util.*;
import java.io.*;

public class BookingManager extends BaseFileService<Ticket> {

    private static final String SEATS_FILE = "booked_seats.csv";
    private static final String TICKETS_FILE = "tickets.csv";

    public BookingManager() {
        super(TICKETS_FILE);
        initializeSeatsFile();
    }

    @Override
    protected Ticket parseFromString(String line) {
        String[] parts = line.split(",");
        if (parts.length >= 8) {
            List<String> seats = Arrays.asList(parts[5].split(";"));
            Map<String, Integer> snacks = new HashMap<>();

            if (!parts[6].isEmpty()) {
                String[] snackParts = parts[6].split(";");
                for (String snackPart : snackParts) {
                    String[] snackData = snackPart.split(":");
                    if (snackData.length == 2) {
                        snacks.put(snackData[0], Integer.parseInt(snackData[1]));
                    }
                }
            }

            return new Ticket(
                    parts[0], parts[1], parts[2],
                    LocalDate.parse(parts[3]), parts[4],
                    seats, snacks, Integer.parseInt(parts[7]));
        }
        return null;
    }

    @Override
    protected String convertToString(Ticket ticket) {
        StringBuilder snacksStr = new StringBuilder();
        for (Map.Entry<String, Integer> entry : ticket.getSnacks().entrySet()) {
            if (snacksStr.length() > 0)
                snacksStr.append(";");
            snacksStr.append(entry.getKey()).append(":").append(entry.getValue());
        }

        return ticket.getTicketId() + "," +
                ticket.getCustomerName() + "," +
                ticket.getMovieTitle() + "," +
                ticket.getShowDate().toString() + "," +
                ticket.getShowTime() + "," +
                String.join(";", ticket.getSeats()) + "," +
                snacksStr.toString() + "," +
                ticket.getTotalAmount();
    }

    @Override
    protected String getEntityId(Ticket ticket) {
        return ticket.getTicketId();
    }

    @Override
    public Ticket findById(String ticketId) {
        return loadAll().stream()
                .filter(ticket -> ticket.getTicketId().equals(ticketId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean delete(String ticketId) {
        List<Ticket> allTickets = loadAll();
        boolean removed = allTickets.removeIf(ticket -> ticket.getTicketId().equals(ticketId));
        if (removed) {
            return rewriteFile(allTickets);
        }
        return false;
    }

    public List<String> getBookedSeats(String movieTitle, LocalDate date, String time) {
        List<String> bookedSeats = new ArrayList<>();
        String searchKey = movieTitle + "," + date.toString() + "," + time;

        try (BufferedReader br = new BufferedReader(new FileReader(SEATS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(searchKey)) {
                    String[] parts = line.split(",");
                    for (int i = 3; i < parts.length; i++) {
                        bookedSeats.add(parts[i]);
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bookedSeats;
    }

    public void saveBookedSeats(String movieTitle, LocalDate date, String time, List<String> newSeats) {
        String searchKey = movieTitle + "," + date.toString() + "," + time;
        List<String> allLines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(SEATS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(searchKey)) {
                    line += "," + String.join(",", newSeats);
                    found = true;
                }
                allLines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!found) {
            allLines.add(searchKey + "," + String.join(",", newSeats));
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(SEATS_FILE))) {
            for (String line : allLines) {
                pw.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveTicket(Ticket ticket) {
        save(ticket);
    }

    public List<Ticket> getUserTickets(String username) {
        return loadAll().stream()
                .filter(ticket -> ticket.getCustomerName().equals(username))
                .collect(java.util.stream.Collectors.toList());
    }

    private void initializeSeatsFile() {
        try {
            File seatsFile = new File(SEATS_FILE);
            if (!seatsFile.exists()) {
                seatsFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}