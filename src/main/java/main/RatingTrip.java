package main;

import common.RATING;
public class RatingTrip {
    private String rideId;
    private RATING rating;

    public RatingTrip() {}

    public RatingTrip(String rideId, RATING rating) {
        this.rideId = rideId;
        this.rating = rating;
    }

    public String getRideId() {
        return rideId;
    }

    public RATING getRating() {
        return rating;
    }

    public void setRideId(String rideId) {  // ✅ Correct setter name
        this.rideId = rideId;
    }

    public void setRating(RATING rating) {  // ✅ Correct setter name
        this.rating = rating;
    }
}
