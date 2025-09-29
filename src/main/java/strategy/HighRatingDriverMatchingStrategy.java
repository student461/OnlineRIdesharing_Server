package strategy;

import manager.DriverMgr;
import model.Driver;
import model.TripMetaData;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HighRatingDriverMatchingStrategy implements DriverMatchingStrategy {
    @Override
 
     public Driver matchDriver(List<Driver> driversList, TripMetaData meta, Set<String> rejectedIds) {
    	if (driversList.isEmpty()) {
            System.out.println("No drivers available!");
            return null;
        } 
    	return driversList.stream()  // Works directly with List
             .filter(d -> !rejectedIds.contains(d.getId()))
             .max(Comparator.comparingDouble(d -> d.getRating().getValue()))
             .orElse(null);
     }

     // Returns: Driver("Raj", "D1", BIKE, 4.8, true)
    }

