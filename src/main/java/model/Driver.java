package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import common.RATING;
import common.VEHICLE_TYPE;

public class Driver implements Serializable {
	    private static final long serialVersionUID = 1L;
    private final String name;
    private final String id;
    private Location location;
    private RATING rating;
    private boolean available;
    private Trip matchedTrip;
    private List<Trip> completedTrips; // List to hold completed trips
	private VEHICLE_TYPE vehicleType;
    // Constructor
    public Driver(String name, String id, Location location, RATING rating, boolean available,VEHICLE_TYPE vehicleType) {
        this.name = name;
        this.id = id;
        this.location = location;
        this.rating = rating;
        this.available = available;
        this.matchedTrip = null;
        this.completedTrips = new ArrayList<>();  // Initialize the completedTrips list
        this.vehicleType = vehicleType;
    }

    public List<Trip> getCompletedTrips() {
		return completedTrips;
	}

	public void setCompletedTrips(List<Trip> completedTrips) {
		this.completedTrips = completedTrips;
	}

	public void setVehicleType(VEHICLE_TYPE vehicleType) {
		this.vehicleType = vehicleType;
	}

	// Getters
    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Location getLocation() {
        return location;
    }

    public RATING getRating() {
        return rating;
    }

    public boolean isAvailable() {
        return available;
    }

    public Trip getMatchedTrip() {
        return matchedTrip;
    }

    // Setters
    public void setLocation(Location location) {
        this.location = location;
    }

    public void setRating(RATING rating) {
        this.rating = rating;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setMatchedTrip(Trip matchedTrip) {
        this.matchedTrip = matchedTrip;
    }

    // Helper Methods
    public boolean isMatched() {
        return matchedTrip != null;
    }

    public void setMatched(boolean matched) {
        if (!matched) {
            this.matchedTrip = null;
        }
    }

    // Method to add completed trips to the driver's record
    public void addTrip(Trip trip) {
        this.completedTrips.add(trip);  // Assuming completedTrips is a List<Trip>
    }

    // Method to display driver details
    public void displayDetails() {
        System.out.println("Driver ID: " + id);
        System.out.println("Driver Name: " + name);
        System.out.println("Location: (" + location.getLatitude() + ", " + location.getLongitude() + ")");
        System.out.println("Availability: " + (available ? "Available" : "Not Available"));
        System.out.println("Rating: " + rating);
        System.out.println("Completed Trips: " + completedTrips.size());
    }

    public VEHICLE_TYPE getVehicleType() {
        return this.vehicleType; // Return enum instead of String
    }
}
