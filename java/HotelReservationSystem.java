import java.io.*;
import java.util.*;

// Room class
class Room {
    private int roomNumber;
    private String category;
    private boolean isAvailable;

    public Room(int roomNumber, String category) {
        this.roomNumber = roomNumber;
        this.category = category;
        this.isAvailable = true;
    }

    public int getRoomNumber() { return roomNumber; }
    public String getCategory() { return category; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { this.isAvailable = available; }

    @Override
    public String toString() {
        return "Room " + roomNumber + " (" + category + ") - " + 
               (isAvailable ? "Available" : "Booked");
    }
}

// Reservation class
class Reservation {
    private String customerName;
    private Room room;
    private String paymentStatus;

    public Reservation(String customerName, Room room) {
        this.customerName = customerName;
        this.room = room;
        this.paymentStatus = "Pending";
    }

    public String getCustomerName() { return customerName; }
    public Room getRoom() { return room; }
    public String getPaymentStatus() { return paymentStatus; }

    public void makePayment() {
        this.paymentStatus = "Paid";
    }

    @Override
    public String toString() {
        return "Reservation: " + customerName + " booked " + room.getCategory() +
               " Room " + room.getRoomNumber() + " | Payment: " + paymentStatus;
    }
}

// Hotel class
class Hotel {
    private List<Room> rooms = new ArrayList<>();
    private List<Reservation> reservations = new ArrayList<>();
    private final String FILE_NAME = "reservations.txt";

    public Hotel() {
        // Initialize rooms
        rooms.add(new Room(101, "Standard"));
        rooms.add(new Room(102, "Deluxe"));
        rooms.add(new Room(103, "Suite"));
        rooms.add(new Room(104, "Standard"));
        rooms.add(new Room(105, "Deluxe"));
        loadReservations();
    }

    public void searchRooms(String category) {
        System.out.println("Available " + category + " rooms:");
        for (Room room : rooms) {
            if (room.getCategory().equalsIgnoreCase(category) && room.isAvailable()) {
                System.out.println(room);
            }
        }
    }

    public void bookRoom(String customerName, int roomNumber) {
        for (Room room : rooms) {
            if (room.getRoomNumber() == roomNumber && room.isAvailable()) {
                room.setAvailable(false);
                Reservation reservation = new Reservation(customerName, room);
                reservations.add(reservation);
                saveReservations();
                System.out.println("Booking successful for " + customerName);
                return;
            }
        }
        System.out.println("Room not available!");
    }

    public void cancelReservation(String customerName, int roomNumber) {
        Iterator<Reservation> iterator = reservations.iterator();
        while (iterator.hasNext()) {
            Reservation res = iterator.next();
            if (res.getCustomerName().equalsIgnoreCase(customerName) &&
                res.getRoom().getRoomNumber() == roomNumber) {
                res.getRoom().setAvailable(true);
                iterator.remove();
                saveReservations();
                System.out.println("Reservation cancelled for " + customerName);
                return;
            }
        }
        System.out.println("No reservation found!");
    }

    public void viewReservations() {
        System.out.println("All Reservations:");
        for (Reservation res : reservations) {
            System.out.println(res);
        }
    }

    public void makePayment(String customerName, int roomNumber) {
        for (Reservation res : reservations) {
            if (res.getCustomerName().equalsIgnoreCase(customerName) &&
                res.getRoom().getRoomNumber() == roomNumber) {
                res.makePayment();
                saveReservations();
                System.out.println("Payment successful for " + customerName);
                return;
            }
        }
        System.out.println("Reservation not found!");
    }

    // File I/O persistence
    private void saveReservations() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Reservation res : reservations) {
                bw.write(res.getCustomerName() + "," + res.getRoom().getRoomNumber() + "," +
                         res.getRoom().getCategory() + "," + res.getPaymentStatus());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving reservations.");
        }
    }

    private void loadReservations() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String customerName = parts[0];
                int roomNumber = Integer.parseInt(parts[1]);
                String category = parts[2];
                String paymentStatus = parts[3];

                for (Room room : rooms) {
                    if (room.getRoomNumber() == roomNumber) {
                        room.setAvailable(false);
                        Reservation res = new Reservation(customerName, room);
                        if (paymentStatus.equals("Paid")) res.makePayment();
                        reservations.add(res);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading reservations.");
        }
    }
}

// Main class
public class HotelReservationSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Hotel hotel = new Hotel();

        while (true) {
            System.out.println("\n--- Hotel Reservation System ---");
            System.out.println("1. Search Rooms");
            System.out.println("2. Book Room");
            System.out.println("3. Cancel Reservation");
            System.out.println("4. View Reservations");
            System.out.println("5. Make Payment");
            System.out.println("6. Exit");
            System.out.print("Choose option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter category (Standard/Deluxe/Suite): ");
                    String category = sc.nextLine();
                    hotel.searchRooms(category);
                    break;
                case 2:
                    System.out.print("Enter your name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter room number: ");
                    int roomNum = sc.nextInt();
                    hotel.bookRoom(name, roomNum);
                    break;
                case 3:
                    System.out.print("Enter your name: ");
                    String cname = sc.nextLine();
                    System.out.print("Enter room number: ");
                    int rnum = sc.nextInt();
                    hotel.cancelReservation(cname, rnum);
                    break;
                case 4:
                    hotel.viewReservations();
                    break;
                case 5:
                    System.out.print("Enter your name: ");
                    String pname = sc.nextLine();
                    System.out.print("Enter room number: ");
                    int pnum = sc.nextInt();
                    hotel.makePayment(pname, pnum);
                    break;
                case 6:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}
