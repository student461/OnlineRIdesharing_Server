package model;

import common.RATING;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Rider implements Serializable {
	
	    private static final long serialVersionUID = 1L;
    private final String name;
    private final String id;
    private final Location source;
    private final Location destination;
    private final RATING rating;
    private List<Trip> previousTrips;  // List to store previous trips

    // Constructor
    public Rider(String name, String id, Location source, Location destination, RATING rating) {
        this.name = name;
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.rating = rating;
        this.previousTrips = new ArrayList<>();  // Initialize the previous trips list
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Location getSource() {
        return source;
    }

    public Location getDestination() {
        return destination;
    }

    public RATING getRating() {
        return rating;
    }

    // Method to add a trip to the previous trips list
    public void addTrip(Trip trip) {
        this.previousTrips.add(trip);
    }

    // Method to retrieve the list of previous trips
    public List<Trip> getPreviousTrips() {
        return previousTrips;
    }
  
    
}
