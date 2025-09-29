package main;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import common.RATING;
import model.Location;
import model.TripResponse;

@Controller
public class DriverClient {
    
@Autowired
private SimpMessagingTemplate messagingTemplate; 

@MessageMapping("/driverAccept")
public void AcceptTrip() {
    // You’ll need a way to signal this to the socket logic. Here’s one simple way:
    System.out.println("called from frontend");
    getresponse.setResponse_S(false);
    getresponse.setResponseDstat(true);
}

@MessageMapping("/driverReject")
public void DeclineTrip() {
    // You’ll need a way to signal this to the socket logic. Here’s one simple way:
    System.out.println("called from frontend");
    getresponse.setResponse_S(false);
    getresponse.setResponseDstat(false);
}

@MessageMapping("/endTripDriver")
public void endTripDriver() {
    // You’ll need a way to signal this to the socket logic. Here’s one simple way:
    System.out.println("called from frontend");

    getresponse.setResponse_end(true);
    
}

@MessageMapping("/paymentReceived")
public void Payemnt() {
    // You’ll need a way to signal this to the socket logic. Here’s one simple way:
    System.out.println("called from frontend pament api");
   
    getresponse.setPayment(true);
    messagingTemplate.convertAndSend("/topic/greetingsDriver", new Payment(true));
}



@MessageMapping("/createTripDriver")
public void handleTrip(DriverRequest testDrive) {
	System.out.println("handle trip is called");
        try (Socket socket = new Socket("localhost", 12345);
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

            output.writeObject("DRIVER"); // Identify as a Driver Client
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter your name: ");
            String name = testDrive.getName();
            output.writeObject(name);

            System.out.print("Enter your ID: ");
            String id = testDrive.getId();
            output.writeObject(id);

            System.out.print("Enter your current location (latitude longitude): ");
            double dstLat = testDrive.getFromlat();
            double dstLon = testDrive.getFromlon();
            output.writeObject(new Location(dstLat, dstLon));

            System.out.print("Enter your rating (1.0 to 5.0): ");
            double ratingValue = testDrive.getRating();
            RATING rating = RATING.fromValue(ratingValue);
            output.writeObject(rating);

            String regStat = (String) input.readObject();
            System.out.println(regStat); // Registration success message
            // messagingTemplate.convertAndSend("/topic/greetings", new TripResponse(regStat, "...", "..."));
            while (true) {
                String rideRequest = (String) input.readObject();
                System.out.println(rideRequest);
                messagingTemplate.convertAndSend("/topic/greetingsDriver", new TripResponse(rideRequest, "...", "..."));
                String prompt = (String) input.readObject();
                System.out.println(prompt);

                System.out.print("Your response (yes/no): ");
                messagingTemplate.convertAndSend("/topic/greetingsDriver", new TripResponse(null, null, "Your response (yes/no):"));
                
                while(getresponse.isResponse_S())
                {}
                System.out.println("after while");
                getresponse.setResponse_S(true);
                boolean response = getresponse.isResponseDstat();
                String confirmation = null;
                if(response) {
                	confirmation = "yes";
                }
                else
                {
                	confirmation = "no";
                }
                output.writeObject(confirmation);

                if (getresponse.isResponseDstat()) {
                    System.out.println((String) input.readObject()); // Ride started message
                
                    // Wait for "end" input
                    while (!getresponse.isResponse_end() && !getresponse.isPayment()) {
                        
                    }
                    getresponse.setResponse_end(false);
                    getresponse.setPayment(false);
                    output.writeObject("end"); // Notify server about the end
                    String tripCompletionMsg = (String) input.readObject(); // Wait for server confirmation
                    System.out.println(tripCompletionMsg); // Ride completion message
                    System.out.println("Ride completed. Waiting for new requests...");
                } else {
                    System.out.println((String) input.readObject()); // Ride declined message
                }
                
                
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
