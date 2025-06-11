package finalLab.Service;

import finalLab.Model.Movie;
import finalLab.Model.SnackItem;
import finalLab.Model.Ticket;
import finalLab.Model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TicketService {
    private final String ticketDataFile = "tickets/tickets.txt";

    public List<String> getBookedSeats(Movie movie, String date, String schedule) {
        List<String> bookedSeats = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ticketDataFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length < 6) continue;
                String movieTitle = parts[2];
                String bookedDate = parts[3];
                String bookedSchedule = parts[4];
                String bookedSeat = parts[5];

                if (movieTitle.equals(movie.getTitle())
                        && bookedDate.equals(date)
                        && bookedSchedule.equals(schedule)) {
                    bookedSeats.add(bookedSeat);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bookedSeats;
    }

    public boolean isSeatAvailable(Movie movie, String date, String schedule, String seat) {
        File file = new File("tickets/tickets.txt");
        if (!file.exists()) return true;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length < 6) continue;

                String movieTitle = parts[2];
                String bookedDate = parts[3];
                String bookedSchedule = parts[4];
                String bookedSeat = parts[5];

                if (movieTitle.equals(movie.getTitle())
                    && bookedDate.equals(date)
                    && bookedSchedule.equals(schedule)
                    && bookedSeat.equals(seat)) {
                    return false; 
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean tryBookSeat(User user, Movie movie, String date, String schedule, String seat, List<SnackItem> snacks) {
        if (isSeatAvailable(movie, date, schedule, seat)) {
            generateTicket(user, movie, date, schedule, seat, snacks);
            return true;
        } else {
            return false;
        }
    }

    public String generateTicket(User user, Movie movie, String date, String schedule, String seat, List<SnackItem> snacks) {
        String ticketId = "TKT" + System.currentTimeMillis(); // lebih readable
        int totalPrice = 50000; // Misal harga default
        StringBuilder snackData = new StringBuilder();

        if (snacks.isEmpty()) {
            snackData.append("- No snacks");
        } else {
            for (SnackItem item : snacks) {
                snackData.append("  - ")
                        .append(item.getSnack().getName())
                        .append(" x")
                        .append(item.getQuantity())
                        .append("\n");
            }
        }

        StringBuilder ticketBlock = new StringBuilder();
        ticketBlock.append("|========== MOVIE TICKET ==========|\n");
        ticketBlock.append("Ticket ID      : ").append(ticketId).append("\n");
        ticketBlock.append("Date           : ").append(java.time.LocalDateTime.now()).append("\n");
        ticketBlock.append("Customer       : ").append(user.getFullName()).append("\n");
        ticketBlock.append("Movie          : ").append(movie.getTitle()).append("\n");
        ticketBlock.append("Date           : ").append(date).append("\n");
        ticketBlock.append("Schedule       : ").append(schedule).append("\n");
        ticketBlock.append("Seat           : ").append(seat).append("\n");
        ticketBlock.append("Snacks         :\n").append(snackData);
        ticketBlock.append("Total Amount   : Rp ").append(totalPrice).append("\n");
        ticketBlock.append("Status         : PAID\n");
        ticketBlock.append("|==================================|\n");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ticketDataFile, true))) {
            writer.write(ticketBlock.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ticketId;
    }

    public void bookSeat(User user, Movie movie, String date, String schedule, String seat, List<SnackItem> snacks) {
        generateTicket(user, movie, date, schedule, seat, snacks);
    }

    public List<Ticket> getTicketsByUser(User user) {
        List<Ticket> tickets = new ArrayList<>();
        File file = new File("tickets/tickets.txt");
        if (!file.exists()) return tickets;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            Ticket ticket = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("|==========")) {
                    ticket = new Ticket();
                } else if (ticket != null) {
                    if (line.startsWith("Ticket ID")) ticket.setTicketId(line.split(":", 2)[1].trim());
                    if (line.startsWith("Customer")) ticket.setUsername(line.split(":", 2)[1].trim());
                    if (line.startsWith("Movie")) ticket.setMovieTitle(line.split(":", 2)[1].trim());
                    if (line.startsWith("Date") && ticket.getDate() == null) ticket.setDate(line.split(":", 2)[1].trim());
                    if (line.startsWith("Schedule")) ticket.setSchedule(line.split(":", 2)[1].trim());
                    if (line.startsWith("Seat")) ticket.setSeat(line.split(":", 2)[1].trim());
                    if (line.startsWith("|==================================|")) {
                        if (ticket.getUsername().equals(user.getUsername())) {
                        tickets.add(ticket);
                        }
                        ticket = null;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tickets;
    }

    public void cancelTicket(String ticketId) {
        File inputFile = new File(ticketDataFile);
        File tempFile = new File("tickets_temp.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.startsWith(ticketId + "|")) {
                    continue;
                }
                writer.write(currentLine);
                writer.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (inputFile.delete()) {
            tempFile.renameTo(inputFile);
        }
    }
}