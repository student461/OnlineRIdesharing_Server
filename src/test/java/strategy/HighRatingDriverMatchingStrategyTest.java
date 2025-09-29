

package strategy; // Should match your package structure

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.*;
import common.RATING;
import common.VEHICLE_TYPE;
import model.Driver; // Critical import
import model.Location;
import model.TripMetaData;

class  HighRatingDriverMatchingStrategyTest {

    @Test
    void testHighRatingDriverMatchingStrategy() {
        // 1. Create proper Driver instances using correct constructor
        List<Driver> drivers = Arrays.asList(
            new Driver("ram", "1", new Location(1, 2), RATING.FIVE_STARS, true, VEHICLE_TYPE.BIKE),
            new Driver("Amit", "D2", new Location(1, 1), RATING.FOUR_STARS, true, VEHICLE_TYPE.BIKE),
            new Driver("Vikram", "D3", new Location(2, 2), RATING.FIVE_STARS, true, VEHICLE_TYPE.BIKE)
        );
        System.out.println("Driver created");
        // 2. Prepare test data
        Set<String> rejectedIds = new HashSet<>();
        TripMetaData meta = new TripMetaData(
            new Location(0, 0), // srcLoc
            new Location(10, 10), // dstLoc
            RATING.FIVE_STARS, // riderRating
            VEHICLE_TYPE.BIKE, // vehicleType
            14.14, // distanceKm
            30, // durationMinutes
            LocalDateTime.now() // tripTime
        );
        
        // 3. Execute test
        HighRatingDriverMatchingStrategy strategy = new HighRatingDriverMatchingStrategy();
        Driver result = strategy.matchDriver(drivers, meta, rejectedIds);
        System.out.println("Driver" + result.getId());
        // 4. Verify results
        assertNotNull(result);
        assertThat(result.getId()).isIn("D3", "1");
    }
}