package manager;

import model.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import common.TRIP_STATUS;

public class TripMgr {
    private static TripMgr tripMgrInstance;
    private final Map<String, Trip> trips = new ConcurrentHashMap<>(); // Changed to String keys

    private TripMgr() {}

    public static TripMgr getInstance() {
        if (tripMgrInstance == null) {
            synchronized (TripMgr.class) {
                if (tripMgrInstance == null) {
                    tripMgrInstance = new TripMgr();
                }
            }
        }
        return tripMgrInstance;
    }

    public Trip createTrip(Rider rider, Driver driver, Location src, Location dst, double fare) {
        String tripId = generateTripId();
        Trip trip = new Trip(tripId, rider, driver, src, dst, fare, TRIP_STATUS.DRIVER_ON_THE_WAY);
        trips.put(trip.getTripId(), trip);
        return trip;
    }

    private String generateTripId() {
        // Using UUID with timestamp prefix for better readability
        return "TRIP-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    public Trip getTrip(String tripId) {
        return trips.get(tripId);
    }

    public Map<String, Trip> getAllTrips() {
        return trips;
    }

    public void startTrip(String tripId) {
        Trip trip = trips.get(tripId);
        if (trip != null) {
            trip.setStatus(TRIP_STATUS.STARTED);
        }
    }
    
    public void endTrip(String tripId) {
        Trip trip = trips.get(tripId);
        if (trip != null) {
            trip.setStatus(TRIP_STATUS.ENDED);
        }
    }
}