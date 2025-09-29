package main;

import manager.*;
import model.*;
import service.TripService;
import strategy.*;
import common.*;
import config.SpringContext;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

public class RideSharingServer {
	   
    private static final int PORT = 12345;

    public static void main(String[] args) {
        System.out.println("Server is running...");

        // Start the client handler in a separate thread
        new Thread(() -> startClientHandler()).start();

        // Start the admin menu for server-side operations
        startAdminMenu();
    }

    private static void startClientHandler() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startAdminMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n--- Server Admin Menu ---");
            System.out.println("1. Display Active Riders");
            System.out.println("2. Display Active Drivers");
            System.out.println("3. Display All Trips");
            System.out.println("4. Exit Server");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> displayActiveRiders();
                case 2 -> displayActiveDrivers();
                case 3 -> displayAllTrips();
                case 4 -> {
                    System.out.println("Shutting down the server...");
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void displayActiveRiders() {
        Collection<Rider> riders = RiderMgr.getInstance().getAllRiders();
        System.out.println("\n--- Active Riders ---");
        if (riders.isEmpty()) {
            System.out.println("No active riders.");
        } else {
            for (Rider rider : riders) {
                System.out.println("Name: " + rider.getName() + ", ID: " + rider.getId());
            }
        }
    }

    private static void displayActiveDrivers() {
        Map<String, Driver> drivers = DriverMgr.getInstance().getDriversMap();
        System.out.println("\n--- Active Drivers ---");
        if (drivers.isEmpty()) {
            System.out.println("No active drivers.");
        } else {
            for (Driver driver : drivers.values()) {
                String locationStr = (driver.getLocation() != null) ? driver.getLocation().toString() : "Location not set";
                System.out.println("Name: " + driver.getName() + ", ID: " + driver.getId() +
                        ", Location: " + locationStr + ", Available: " + driver.isAvailable());
            }
        }
    }

    private static void displayAllTrips() {
        Collection<Trip> trips = TripMgr.getInstance().getAllTrips().values();
        System.out.println("\n--- All Trips ---");
        if (trips.isEmpty()) {
            System.out.println("No trips found.");
        } else {
            for (Trip trip : trips) {
                System.out.println("Trip ID: " + trip.getTripId() +
                        ", Rider: " + trip.getRider().getName() +
                        ", Driver: " + trip.getDriver().getName() +
                        ", Fare: $" + trip.getFare() +
                        ", Status: " + trip.getStatus());
            }
        }
    }

    private static class ClientHandler extends Thread {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                 ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream())) {

                String clientType = (String) input.readObject(); // "RIDER" or "DRIVER" or "SERVER"

                switch (clientType) {
                    case "RIDER" -> handleRider(input, output);
                    case "DRIVER" -> handleDriver(input, output);
                    case "SERVER" -> handleServerCommands(input, output);
                }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        private void handleRider(ObjectInputStream input, ObjectOutputStream output) throws IOException, ClassNotFoundException {
            String name = (String) input.readObject();
            String id = (String) input.readObject();
            Location src = null;
            Location dst = null;
            double distanceKm ;
            double durationMinutes;
            
            String strategyChoice = null;
        
            Rider rider = new Rider(name, id, src, dst, RATING.FIVE_STARS);
            RiderMgr.getInstance().addRider(id, rider);
            System.out.println("Rider registered: " + name + " (ID: " + id + ")");
        
            while (true) {
                String command = (String) input.readObject();
        
                if ("VIEW_PREVIOUS_TRIPS".equalsIgnoreCase(command)) {
                    // Respond with previous trip details
                    StringBuilder tripsInfo = new StringBuilder("Previous Trips:\n");
                    for (Trip trip : rider.getPreviousTrips()) {
                        tripsInfo.append("Driver: " + trip.getDriver().getName() +
                                " (ID: " + trip.getDriver().getId() + "), " +
                                ", Location: " + trip.getDstLoc().toString() +
                                ", Fare: $" + trip.getFare() + "\n");
                    }
                    output.writeObject(tripsInfo.toString());
                } else if ("EXIT".equalsIgnoreCase(command)) {
                    // Rider wants to exit
                    output.writeObject("Exiting the system. Goodbye!");
                    return;
                } else if ("CREATE_NEW_TRIP".equalsIgnoreCase(command)) {
                    src = (Location) input.readObject();
                    dst = (Location) input.readObject();
                     durationMinutes= (double) input.readObject();
                     distanceKm =(double) input.readObject();
                    strategyChoice = (String) input.readObject();
        
                    DriverMatchingStrategy matchingStrategy = "HIGH_RATING".equalsIgnoreCase(strategyChoice)
                            ? new HighRatingDriverMatchingStrategy()
                            : new LeastTimeBasedMatchingStrategy();
                            Set<String> rejectedDriverIds = new HashSet<>();//avoid reptation of same driver
                         // Get all available drivers first
                            // Get available drivers
                            Map<String, Driver> allDrivers = DriverMgr.getInstance().getDriversMap();
                            List<Driver> availableDrivers = allDrivers.values().stream()
                                .filter(Driver::isAvailable)
                                .collect(Collectors.toList());
                            VEHICLE_TYPE vehicleType = (VEHICLE_TYPE) input.readObject();
                            System.out.println("inside server "+vehicleType);
                            // Create trip metadata with vehicle type
                            // Get from user input in real implementation
                            TripMetaData metaData = new TripMetaData(
                                    src, dst, rider.getRating(), 
                                    vehicleType, 
                                    distanceKm, 
                                    durationMinutes, 
                                    LocalDateTime.now()
                                );
                            // Then apply vehicle filter
//                            List<Driver> vehicleFilteredDrivers = 
//                                DriverPreFilter.filterByVehicleType(availableDrivers, TripMetaData.getVehicleType());
                            List<Driver> vehicleFilteredDrivers = 
                                    DriverPreFilter.filterByVehicleType(availableDrivers, metaData.getVehicleType());

                            Driver driver = null;// use to research
                            Trip trip =null; 
                            while (driver == null) {
                                driver = matchingStrategy.matchDriver(vehicleFilteredDrivers,metaData, rejectedDriverIds);
                            
                                if (driver == null) {
                                    output.writeObject("No drivers available. Please try again later or type 'EXIT' to quit.");
                                    String retry = (String) input.readObject();
                                    if ("EXIT".equalsIgnoreCase(retry)) break;
                                    continue;
                                }
                            
                                double distance = src.distanceTo(dst);
                                double fare = calculateFare(distance);
                                 trip = TripMgr.getInstance().createTrip(rider, driver, src, dst, fare);
                            
                                 output.writeObject(trip);
                                 output.writeObject("Driver matched: " + driver.getName()  + ", DriverId: " + driver.getId() + " Fare: $" + fare + " ,driver location: " + driver.getLocation().toString());
                                output.writeObject("Awaiting driver's confirmation...");
                                rider.addTrip(trip);
                            
                                synchronized (driver) {
                                    driver.setMatchedTrip(trip);
                                    driver.notifyAll();
                                }
                            
                                synchronized (driver) {
                                    while (driver.getMatchedTrip() != null && !trip.isDriverAccepted()) {
                                        try {
                                            driver.wait();
                                        } catch (InterruptedException e) {
                                            Thread.currentThread().interrupt();
                                        }
                                    }
                                }
                            
                                if (!trip.isDriverAccepted()) {
                                    output.writeObject("Driver rejected the trip. Searching for another driver...");
                                    rejectedDriverIds.add(driver.getId()); // 👈 Add rejected driver
                                    driver = null; // Retry
                                }
                            }
                            
                    if (trip.isDriverAccepted()) {
                        output.writeObject("Driver accepted the ride. Trip started.");
                        output.writeObject(trip);
                        output.writeObject("Type 'end' to finish the ride.");
                        
                        // Synchronization mechanism for ending the trip
boolean riderEnded = false;
boolean driverEnded = false;
System.out.println("indie rider handler");
System.out.println("before while rider side");
while (!riderEnded || !driverEnded) {
	 try {
	        Object inputFromClient = input.readObject();
	        if (inputFromClient instanceof String && "end".equalsIgnoreCase((String)inputFromClient)) {
	           riderEnded = true; // Mark rider as ended
	        System.out.println("before  trip.isriderEnded" + trip.isRiderEnded());
	        trip.setRiderEnded(true);
	        System.out.println("after trip.isriderEnded" + trip.isRiderEnded());
	        output.writeObject("Waiting for the driver to end the trip...");
	        }
	        else {
	        output.writeObject("// Waiting for both Rider and Driver to end the trip");
	    }
	        // Add proper handling for other message types
	    } catch (EOFException e) {
	        System.out.println("Client disconnected");
	        break;
	    } catch (IOException | ClassNotFoundException e) {
	        e.printStackTrace();
	        break;
	    }
    synchronized (trip) {
        trip.setRiderEnded(true);
        trip.notifyAll(); // ✅ Notify driver waiting for rider end
    }
    synchronized (trip) {
        while (!trip.isDriverEnded()) {
            try {
                trip.wait(); // Wait for driver to end the trip
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    driverEnded = true; // Driver has ended the trip
}
System.out.println("after while rider side");

// End the trip
TripMgr.getInstance().endTrip(trip.getTripId());
System.out.println("trip ened ride side");
driver.setAvailable(true);

output.writeObject("Ride completed. Thank you!");

                    } else {
                        output.writeObject("Driver declined the ride. Searching for another driver...");
                    }
                } else {
                    output.writeObject("Invalid option. Try again.");
                }
            }
        }
         

        private void handleDriver(ObjectInputStream input, ObjectOutputStream output) throws IOException, ClassNotFoundException {
            String name = (String) input.readObject();
            String id = (String) input.readObject();
            Location location = (Location) input.readObject();
            RATING rating = (RATING) input.readObject();
        
            Driver driver = new Driver(name, id, location, rating, true, VEHICLE_TYPE.BIKE);
            DriverMgr.getInstance().addDriver(id, driver);
        
            output.writeObject("Driver registered successfully. Waiting for ride requests...");
        
            while (true) {
                synchronized (driver) {
                    while (driver.getMatchedTrip() == null) {
                        try {
                            driver.wait(); // Wait for a trip request
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
        
                Trip trip = driver.getMatchedTrip();
                Rider rider = trip.getRider();
        
                // Notify Driver of Ride Request
                output.writeObject("Ride Request: Rider " + rider.getName() +
                        ", Locationsrc: " + trip.getSrcLoc().toString() +",Locationdst: " + trip.getDstLoc().toString() +
                        ", Fare: $" + trip.getFare());
                output.writeObject("Do you accept the ride? (yes/no)");
        
                // Wait for Driver Response
                String response = (String) input.readObject();
                synchronized (driver) {
                    if ("yes".equalsIgnoreCase(response)) {
                        trip.setDriverAccepted(true); // Mark the trip as accepted
                        driver.setAvailable(false); // Mark the driver as unavailable
                        driver.setMatchedTrip(null); // Clear matched trip
                        TripMgr.getInstance().startTrip(trip.getTripId()); // Start the trip
        
                        // Notify the rider that the trip has started
                        driver.notifyAll();
                        output.writeObject("Ride started. Type 'end' to complete the ride:");
                    } else {
                        trip.setDriverAccepted(false); // Driver declined the trip
                        driver.setMatchedTrip(null);
                         // Clear matched trip
                         driver.notifyAll();
                        output.writeObject("Ride declined. Waiting for new requests...");
                        continue;
                    }
                }
        
                // Wait for the trip to end when both driver and rider type "end"
               // Synchronization mechanism for ending the trip
                boolean riderEnded = false;
                boolean driverEnded = false;
                System.out.println("inside handle driver");
                System.out.println("before while driverside");
                while (!riderEnded || !driverEnded) {
                    String inputFromClient = (String) input.readObject();
                    if ("end".equalsIgnoreCase(inputFromClient)) {
                        driverEnded = true; // Mark driver as ended
                        output.writeObject("Waiting for the rider to end the trip...");
                    } else {
                        output.writeObject("// Waiting for both Rider and Driver to end the trip");
                    }

                    synchronized (trip) {
                        System.out.println("inside driver handler firsst sync");
                        trip.setDriverEnded(true); // Notify that the driver has ended the tri
                        trip.notifyAll(); // Notify the rider's thread waiting on the trip
                        System.out.println("driver handler after notify all");
                    }

                    synchronized (trip) {
                        System.out.println("Inside second sync in driverhandler" );
                        System.out.println("before while trip.isriderEnded" + trip.isRiderEnded());
                        while (!trip.isRiderEnded()) {
                            
                            try {
                                trip.wait(); // Wait for rider to end the trip
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                        System.out.println("After while trip.isriderEnded" + trip.isRiderEnded());
                        

                    }
                    riderEnded = true; // Rider has ended the trip
                }
                System.out.println("after while driver side");

                // End the trip
                TripMgr.getInstance().endTrip(trip.getTripId());
                System.out.println("trip get eneded driver side");
                driver.setAvailable(true);
                driver.setLocation(trip.getDstLoc());

                System.out.println(driver.getLocation().toString());
                // output.writeObject("Ride completed. Waiting for new requests.");

            }
                // When the driver types "end", end the trip
            //     TripMgr.getInstance().endTrip(trip.getTripId()); // End the trip
            //     driver.setAvailable(true); // Make the driver available again
            //     // Update the driver's location to the destination
            //     output.writeObject("Ride completed. Waiting for new requests.");
            // }
        }
                        
        private double calculateFare(double distance) {
            double baseFare = 50; // Base fare
            double perKmRate = 10; // Rate per kilometer
            return baseFare + (distance * perKmRate);
        }

        private void handleServerCommands(ObjectInputStream input, ObjectOutputStream output) throws IOException, ClassNotFoundException {
            String command = (String) input.readObject();

            switch (command) {
                case "DISPLAY_ACTIVE" -> {
                    output.writeObject("Active Riders: " + RiderMgr.getInstance().getAllRiders());
                    output.writeObject("Active Drivers: " + DriverMgr.getInstance().getDriversMap());
                }
                case "DISPLAY_TRIPS" -> {
                    output.writeObject("All Trips: " + TripMgr.getInstance().getAllTrips());
                }
                case "EXIT" -> {
                    output.writeObject("Shutting down the server.");
                    System.exit(0);
                }
                default -> output.writeObject("Unknown command.");
            }
        }
    }
}

