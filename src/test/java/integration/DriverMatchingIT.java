package integration;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import manager.DriverMgr;
import manager.TripMgr;
import model.*;
import strategy.HighRatingDriverMatchingStrategy;
import common.*;

import java.time.LocalDateTime;
import java.util.*;

class DriverMatchingIT {
    
    private DriverMgr driverMgr;
    private TripMgr tripMgr;
    private HighRatingDriverMatchingStrategy strategy;
    
    @BeforeEach
    void setUp() {
        driverMgr = DriverMgr.getInstance();
        tripMgr = TripMgr.getInstance();
        strategy = new HighRatingDriverMatchingStrategy();
        
        // Reset state
        driverMgr.getDriversMap().clear();
        tripMgr.getAllTrips().clear();
    }

    @Test
    void testCompleteRideFlow() {
        // Setup drivers
        driverMgr.addDriver("D1", new Driver("Raj", "D1", 
            new Location(1,1), RATING.FIVE_STARS, true, VEHICLE_TYPE.BIKE));
        driverMgr.addDriver("D2", new Driver("Amit", "D2", 
            new Location(2,2), RATING.FOUR_STARS, true, VEHICLE_TYPE.BIKE));

        // Create trip request
        TripMetaData meta = new TripMetaData(
            new Location(0,0), new Location(10,10),
            RATING.FIVE_STARS, VEHICLE_TYPE.BIKE,
            14.14, 30, LocalDateTime.now()
        );

        // Execute matching
        Driver matchedDriver = strategy.matchDriver(
            new ArrayList<>(driverMgr.getDriversMap().values()),
            meta, new HashSet<>()
        );

        // Create and verify trip
        Trip trip = tripMgr.createTrip(
            new Rider("ram", "1", new Location(1, 2),new Location(4, 5), RATING.FIVE_STARS), 
            matchedDriver,
            meta.getSrcLoc(),
            meta.getDstLoc(),
            calculateFare(meta.getDistanceKm())
        );
        System.out.println("trip creater successfully!!!");
        assertNotNull(trip);
        assertEquals("D1", trip.getDriver().getId());
        assertEquals(TRIP_STATUS.DRIVER_ON_THE_WAY, trip.getStatus());
    }
    
    private double calculateFare(double distance) {
        return distance * 10; // Simple fare calculation
    }
}