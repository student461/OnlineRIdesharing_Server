package main;

import org.springframework.stereotype.Component;

import java.io.ObjectOutputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Component
public class DriverSessionStore {
    private final Map<String, ObjectOutputStream> driverOutputMap = new ConcurrentHashMap<>();

    public void storeDriverStream(String driverId, ObjectOutputStream out) {
        driverOutputMap.put(driverId, out);
    }

    public void respondToDriver(String driverId, String response) {
        try {
            ObjectOutputStream out = driverOutputMap.get(driverId);
            if (out != null) {
                out.writeObject(response); // Send "yes" or "no"
                out.flush();
            } else {
                System.out.println("No stream found for driver ID: " + driverId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
