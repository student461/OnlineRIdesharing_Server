package integration;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import manager.TripMgr;
import model.*;
import common.TRIP_STATUS;
import java.util.Map;

class TripManagementIT {
    
    private TripMgr tripMgr;
    private Rider testRider;
    private Driver testDriver;
    
    @BeforeEach
    void setUp() {
        tripMgr = TripMgr.getInstance();
        tripMgr.getAllTrips().clear(); // Reset state
        
        testRider = new Rider("Rider1", "rider1@example.com");
        testDriver = new Driver("Driver1", "D1", 
            new Location(1,1), RATING.FIVE_STAR, true, VEHICLE_TYPE.BIKE);
    }

    @Test
    void testTripLifecycle() {
        // 1. Create Trip
        Trip trip = tripMgr.createTrip(
            testRider,
            testDriver,
            new Location(0,0),
            new Location(10,10),
            150.0
        );
        
        assertNotNull(trip);
        assertEquals(TRIP_STATUS.DRIVER_ON_THE_WAY, trip.getStatus());
        
        // 2. Verify Trip in Manager
        Trip retrievedTrip = tripMgr.getTrip(trip.getTripId());
        assertSame(trip, retrievedTrip);
        
        // 3. Start Trip
        tripMgr.startTrip(trip.getTripId());
        assertEquals(TRIP_STATUS.STARTED, trip.getStatus());
        
        // 4. End Trip
        tripMgr.endTrip(trip.getTripId());
        assertEquals(TRIP_STATUS.ENDED, trip.getStatus());
        
        // 5. Verify All Trips
        Map<Integer, Trip> allTrips = tripMgr.getAllTrips();
        assertEquals(1, allTrips.size());
        assertTrue(allTrips.containsValue(trip));
    }

    @Test
    void testConcurrentTripCreation() {
        // Simulate 10 concurrent trip requests
        Runnable createTrip = () -> {
            tripMgr.createTrip(
                new Rider("TempRider", "temp@email.com"),
                new Driver("TempDriver", "TD1", new Location(1,1), 
                    RATING.FOUR_STAR, true, VEHICLE_TYPE.BIKE),
                new Location(0,0),
                new Location(5,5),
                100.0
            );
        };
        
        // Run multiple threads
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(createTrip);
            threads[i].start();
        }
        
        // Wait for completion
        for (Thread t : threads) {
            try { t.join(); } catch (InterruptedException e) {}
        }
        
        // Verify all trips created with unique IDs
        assertEquals(10, tripMgr.getAllTrips().size());
        long uniqueIds = tripMgr.getAllTrips().keySet().stream().distinct().count();
        assertEquals(10, uniqueIds);
    }
}