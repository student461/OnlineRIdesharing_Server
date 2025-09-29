package model;

import java.io.Serializable;

import common.TRIP_STATUS;

public class Trip implements Serializable {
    private static final long serialVersionUID = 1L;
    private Rider rider;
    private Driver driver;
    private Location srcLoc;
    private Location dstLoc;
    private double fare;
    private double rating;
    private String tripId;
    private TRIP_STATUS status; // Status of the trip
    private boolean driverAccepted; // If the driver has accepted the trip
    private boolean riderEnded; // If the rider has ended the trip
    private boolean driverEnded; // If the driver has ended the trip

    // Constructor
    public Trip(String tripId, Rider rider, Driver driver, Location srcLoc, Location dstLoc, double fare, TRIP_STATUS status) {
        if (rider == null || driver == null || srcLoc == null || dstLoc == null) {
            throw new IllegalArgumentException("Trip must have a rider, driver, source, and destination.");
        }
        this.tripId = tripId;
        this.rider = rider;
        this.driver = driver;
        this.srcLoc = srcLoc;
        this.dstLoc = dstLoc;
        this.fare = fare;
        this.status = status;
        this.driverAccepted = false; // Default to not accepted
        this.riderEnded = false; // Default to not ended
        this.driverEnded = false; // Default to not ended
    }

    // Synchronized methods for driver acceptance
    public synchronized boolean isDriverAccepted() {
        return driverAccepted;
    }

    public synchronized void setDriverAccepted(boolean driverAccepted) {
        this.driverAccepted = driverAccepted;
        if (driverAccepted) {
            this.status = TRIP_STATUS.DRIVER_ON_THE_WAY; // Set status to DRIVER_ON_THE_WAY when accepted
        }
    }

    // Synchronized methods for trip end tracking
    public synchronized boolean isRiderEnded() {
        return riderEnded;
    }

    public synchronized void setRiderEnded(boolean riderEnded) {
        this.riderEnded = riderEnded;
        if (riderEnded && driverEnded) {
            this.status = TRIP_STATUS.ENDED; // Set status to ENDED when both rider and driver end the trip
        }
    }

    public synchronized boolean isDriverEnded() {
        return driverEnded;
    }

    public synchronized void setDriverEnded(boolean driverEnded) {
        this.driverEnded = driverEnded;
        if (riderEnded && driverEnded) {
            this.status = TRIP_STATUS.ENDED; // Set status to ENDED when both rider and driver end the trip
        }
    }


    // Getters
    public TRIP_STATUS getStatus() {
        return status;
    }

    public String getTripId() {
        return tripId;
    }

    public Rider getRider() {
        return rider;
    }

    public Driver getDriver() {
        return driver;
    }

    public Location getSrcLoc() {
        return srcLoc;
    }

    public Location getDstLoc() {
        return dstLoc;
    }

    public double getFare() {
        return fare;
    }

    public double getRating() {
        return rating;
    }

    // Setters
    public void setStatus(TRIP_STATUS status) {
        this.status = status;
    }

    public void setRating(double rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }
        this.rating = rating;
    }

    // Method to end the trip for both rider and driver
    public void endTrip() {
        this.status = TRIP_STATUS.ENDED;
        this.driverEnded = true;
        this.riderEnded = true;
        // Log the trip as completed
        this.rider.addTrip(this); // Add trip to rider's history
        this.driver.addTrip(this); // Add trip to driver's history
    }

    // Display trip details
    public void displayDetails() {
        System.out.println("Trip ID: " + tripId);
        System.out.println("Rider: " + rider.getName());
        System.out.println("Driver: " + driver.getName());
        System.out.println("Price: $" + fare);
        System.out.println("Source: (" + srcLoc.getLatitude() + ", " + srcLoc.getLongitude() + ")");
        System.out.println("Destination: (" + dstLoc.getLatitude() + ", " + dstLoc.getLongitude() + ")");
        System.out.println("Status: " + status);
    }
}
