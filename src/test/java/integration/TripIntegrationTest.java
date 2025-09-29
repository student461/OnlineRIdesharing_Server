package integration;

import manager.DriverMgr;
import manager.TripMgr;
import model.Driver;
import model.Location;
import model.Rider;
import model.Trip;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TripIntegrationTest {

    DriverMgr driverMgr;
    TripMgr tripMgr;

    @BeforeEach
    void setup() {
        driverMgr = DriverMgr.getInstance();
        tripMgr = TripMgr.getInstance();

        // Clear previous state if needed
        driverMgr.getDriversMap().clear();
        tripMgr.getAllTrips().clear();
    }

    @Test
    void testTripCreationFlow() {
        // Step 1: Create and register a driver
        Driver driver = new Driver("driver1", 4.8); // assuming constructor
        driver.setCurrentLocation(new Location(10.0, 20.0)); // assuming setter
        driverMgr.addDriver("driver1", driver);

        // Step 2: Create a rider
        Rider rider = new Rider("rider1");
        Location from = new Location(10.1, 20.1);
        Location to = new Location(11.0, 21.0);

        // Step 3: Create a trip
        Trip trip = tripMgr.createTrip(rider, driver, from, to, 100.0);

        // Step 4: Assert the trip details
        assertNotNull(trip);
        assertEquals("rider1", trip.getRider().getName());
        assertEquals("driver1", trip.getDriver().getName());
        assertEquals(from.getLatitude(), trip.getSource().getLatitude());
        assertEquals(to.getLatitude(), trip.getDestination().getLatitude());
        assertEquals(100.0, trip.getFare());
        assertEquals("DRIVER_ON_THE_WAY", trip.getStatus().toString());
    }
}
