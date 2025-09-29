package strategy;

import model.Driver;
import model.Location;
import model.TripMetaData;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class LeastTimeBasedMatchingStrategy implements DriverMatchingStrategy {
    @Override
    public Driver matchDriver(List<Driver> driversList, TripMetaData meta, Set<String> rejectedIds) {
        if (driversList.isEmpty()) {
            System.out.println("No drivers available!");
            return null;
        }

        Location riderLocation = meta.getSrcLoc();

        return driversList.stream()
            .filter(driver -> driver.isAvailable() && !rejectedIds.contains(driver.getId()))
            .min(Comparator.comparingDouble(
                d -> d.getLocation().distanceTo(riderLocation)))
            .orElse(null);
    }
}