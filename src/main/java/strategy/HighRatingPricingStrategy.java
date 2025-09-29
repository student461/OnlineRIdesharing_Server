package strategy;


import model.TripMetaData;

public class HighRatingPricingStrategy implements PricingStrategy {
    @Override
    public double calculatePrice(TripMetaData tripMetaData) {
        System.out.println("Based on high-rating strategy, price is 85.0");
        return 85.0; // Lower price for high-rating riders
    }
}
