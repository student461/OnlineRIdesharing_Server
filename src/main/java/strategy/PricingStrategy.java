package strategy;

import model.TripMetaData;

public interface PricingStrategy {
    double calculatePrice(TripMetaData tripMetaData);
}
