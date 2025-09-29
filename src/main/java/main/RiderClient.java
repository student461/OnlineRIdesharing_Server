package main;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import common.VEHICLE_TYPE;
import model.Location;
import model.Trip;
import model.TripResponse;
import service.TripService;

@Controller
public class RiderClient {

@Autowired
private SimpMessagingTemplate messagingTemplate;
@Autowired
private TripService tripService;


private static final Map<String, CompletableFuture<Double>> pendingRatings = new ConcurrentHashMap<>();

@MessageMapping("/rate")
public void receiveRating(Map<String, Object> ratingData) {
    String rideId = (String) ratingData.get("rideId");
    System.out.println("rideId"+rideId);
    Double ratingValue = ((Number) ratingData.get("rating")).doubleValue();
System.out.println("\n\ncalled from client\n");
    CompletableFuture<Double> future = pendingRatings.get(rideId);
    if (future != null) {
        future.complete(ratingValue);  // Unblocks waiting code
        pendingRatings.remove(rideId); // Clean up
        System.out.println("✅ Received rating for ride " + rideId + ": " + ratingValue);
    } else {
        System.out.println("⚠️ No pending rating future for ride " + rideId);
    }
}


    @MessageMapping("/createTrip")
    public void handleTrip(TripRequest test) {
        try (Socket socket = new Socket("localhost", 12345);
        		
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

            output.writeObject("RIDER"); // Identify as a Rider Client
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter your name: ");
            String name = test.getName();
            output.writeObject(name);

            System.out.print("Enter your ID: ");
            String id = test.getId();
            output.writeObject(id);

            output.writeObject("CREATE_NEW_TRIP");

            System.out.print("\nEnter source location (latitude longitude): ");
            double srcLat = test.getFromlat();
            double srcLon = test.getFromlon();
            output.writeObject(new Location(srcLat, srcLon));
            
//            output.writeObject(new Location(srcLat, srcLon));
    
            System.out.print("Enter destination location (latitude longitude): ");
            double dstLat = test.getTolat();
            double dstLon = test.getTolon();
            output.writeObject(new Location(dstLat, dstLon));
    
            double duration = (double)test.getDuration();
            double distance = test.getDistance();
            output.writeObject(duration);
            output.writeObject(distance);
            System.out.println("Choose matching strategy:");
            System.out.println("1. Least Time-Based");
            System.out.println("2. High Rating");
            String strategyChoice = test.getPreference();
            
//            scanner.nextLine(); // Consume newline
            // output.writeObject(strategyChoice == 1 ? "LEAST_TIME" : "HIGH_RATING");
            output.writeObject(strategyChoice);
            String VehicleChoice = test.getVehiclepreference();
            VEHICLE_TYPE vehicle = VEHICLE_TYPE.fromValue(VehicleChoice);
            System.out.println(vehicle);
            output.writeObject(vehicle);
            createNewTrip(output, input, messagingTemplate,scanner,this.tripService);


        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @MessageMapping("/endTrip")
    public void endTrip() {
        // You’ll need a way to signal this to the socket logic. Here’s one simple way:
        System.out.println("called from frontend");
    	EndTripSignal.setEnded(true);
    }
    
    private static void createNewTrip(ObjectOutputStream output, ObjectInputStream input, SimpMessagingTemplate messagingTemplate,Scanner scanner,TripService tripService) throws IOException, ClassNotFoundException {
        // Send command to server
    	 String driverId = null;
        Trip trip = (Trip)input.readObject();
        tripService.createTrip(trip);
        // Server Response
//        System.out.println("\n" + input.readObject()); // Matched driver info
        String driverInfo = (String) input.readObject();
        messagingTemplate.convertAndSend("/topic/greetings", new TripResponse(driverInfo, "...", "..."));
       
        System.out.println((String) input.readObject()); // Awaiting driver's confirmation
        System.out.println("Waiting for server response...");
//		String rideStatus = (String) input.readObject();
//		System.out.println("Ride Status: " + rideStatus);
		// Later...
		String rideStatus = (String) input.readObject();
		messagingTemplate.convertAndSend("/topic/greetings", new TripResponse(null, rideStatus, "Ride in progress..."));

		if (rideStatus.contains("accepted")) {
    // Debug: Check if the program reaches here
    System.out.println("Now waiting for 'end' input...");

    System.out.println("Waiting for 'end' from WebSocket...");
    trip = (Trip)input.readObject();
    String rideend = (String) input.readObject();
    System.out.println(rideend);
    
    System.out.println("received trip");
    tripService.updateTrip(trip);
System.out.println("ended value :" + EndTripSignal.isEnded());
    while (!EndTripSignal.isEnded()) {
        try {
            Thread.sleep(500); // Sleep for 0.5 second to avoid CPU overload
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupt status
            System.out.println("Thread interrupted while waiting for trip to end.");
            break;
        }
    }

    System.out.println("Ride ended by user.");


    // Notify the server that the ride has ended
    output.writeObject("end");

    // Continue reading messages until the server confirms trip completion
    while (true) {
        String serverMsg = (String) input.readObject();
        System.out.println(serverMsg);

        if (serverMsg.contains("Ride completed")) {
            break; // Exit loop when the trip is complete
        }
    }
    String regex = "DriverId: (\\S+)";  // "\\S+" matches any non-whitespace characters
    
    // Create a pattern object
    Pattern pattern = Pattern.compile(regex);
    
    // Create a matcher object
    Matcher matcher = pattern.matcher(driverInfo);
    
    // Check if the pattern matches
    if (matcher.find()) {
        // Extract the DriverId from the matched group
     driverId = matcher.group(1);  // Group 1 corresponds to the first captured group (DriverId)
        System.out.println("Extracted DriverId: " + driverId);
    } else {
        System.out.println("DriverId not found!");
    }
    
    messagingTemplate.convertAndSend("/topic/greetings", new TripResponse(null, null, "Ride ended"));
    String rideId = driverId; // Use real ride ID from earlier
System.out.println("rideID "+rideId);
    CompletableFuture<Double> ratingFuture = new CompletableFuture<>();
    pendingRatings.put(rideId, ratingFuture);

    try {
        double rating = ratingFuture.get();  // This will block until client sends rating
        System.out.println("🎉 Got rating from client: " + rating);
        
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();  // Restore interrupt flag
        System.err.println("Rating wait was interrupted.");
    } catch (ExecutionException e) {
        System.err.println("Failed to get rating: " + e.getMessage());
    }


    // Now you can update DB, perform calculations, etc.

    
// wait and get rating here
    

}

        
    }

    private static void viewPreviousTrips(ObjectOutputStream output, ObjectInputStream input) throws IOException, ClassNotFoundException {
        output.writeObject("VIEW_PREVIOUS_TRIPS"); // Request previous trips from server

        String previousTrips = (String) input.readObject(); // Read server response
        if (previousTrips.isEmpty()) {
            System.out.println("No previous trips found.");
        } else {
            System.out.println("\nPrevious Trips:");
            System.out.println(previousTrips);
        }
    }
}
