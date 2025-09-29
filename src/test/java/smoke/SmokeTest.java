package smoke;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import manager.*;
import model.*;
import strategy.*;
import common.*;
import java.time.LocalDateTime;

public class SmokeTest {

    @Test
    void smokeTestCriticalFlow() {
        // 1. Initialize managers
        DriverMgr driverMgr = DriverMgr.getInstance();
        TripMgr tripMgr = TripMgr.getInstance();
        PricingStrategy pricing = new DefaultPricingStrategy();

        // 2. Add a test driver
        Driver driver = new Driver(
            "TestDriver", "TD1", 
            new Location(1, 1), 
            RATING.FIVE_STARS, 
            true, 
            VEHICLE_TYPE.BIKE
        );
        driverMgr.addDriver("TD1", driver);

        // 3. Create a trip request
        TripMetaData meta = new TripMetaData(
            new Location(0, 0), 
            new Location(5, 5), 
            RATING.FIVE_STARS, 
            VEHICLE_TYPE.BIKE,
            7.07,  // Distance
            15,    // Minutes
            LocalDateTime.now()
        );

        // 4. Calculate fare (pricing smoke test)
        double fare = pricing.calculatePrice(meta);
        assertTrue(fare > 0, "Fare calculation failed");

        // 5. Create trip (trip management smoke test)
        Rider rider =new Rider("ram", "1", new Location(1, 2),new Location(4, 5), RATING.FIVE_STARS);
        Trip trip = tripMgr.createTrip(
            rider, 
            driver, 
            meta.getSrcLoc(), 
            meta.getDstLoc(), 
            fare
        );
        assertNotNull(trip, "Trip creation failed");
        assertEquals(TRIP_STATUS.DRIVER_ON_THE_WAY, trip.getStatus());

        System.out.println("SMOKE TEST PASSED: Core flow works!");
    }
}