package main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

import model.drivers;
import repository.UserDriverRepository;
import common.RATING;

@Controller
public class TripRating {

    @Autowired
    private UserDriverRepository userDriverRepository;

    @MessageMapping("/Rating_Trip")
    public void handleTrip(RatingTrip tripRating) {
        String driverId = tripRating.getRideId();
        System.out.println("driverId" + driverId);
        String clerkId = driverId;
        System.out.println("clerkId: "+clerkId);
        System.out.println("Searching for clerkId: [" + clerkId + "]");

        RATING newRating = tripRating.getRating();
       

        List<drivers> allDrivers = userDriverRepository.findAll();
        for (drivers d : allDrivers) {
            System.out.println("Driver clerkId: " + d.getClerkId());
        }

        Optional<drivers> driverOpt = userDriverRepository.findByclerkId(clerkId);
        

        if (driverOpt.isPresent()) {
            drivers driver = driverOpt.get();
            int oldTotalRating = driver.getTotalRating();
            int oldRatingCount = driver.getRatingCount();

            int numericRating = (int) newRating.getValue();
            int updatedTotalRating = oldTotalRating + numericRating;
            int updatedRatingCount = oldRatingCount + 1;
            int avgRating = updatedTotalRating / updatedRatingCount;

            driver.setTotalRating(updatedTotalRating);
            driver.setRatingCount(updatedRatingCount);
            driver.setRating(avgRating);

            userDriverRepository.save(driver);
        } else {
            System.out.println("Driver not found: " + driverId);
        }
    }
}
