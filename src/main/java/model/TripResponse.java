package model;

public class TripResponse {
    public String driverInfo;
    public String rideStatus;
    public String message;

    public TripResponse() {}

    public TripResponse(String driverInfo, String rideStatus, String message) {
        this.driverInfo = driverInfo;
        this.rideStatus = rideStatus;
        this.message = message;
    }

    // Getters and Setters
    public String getDriverInfo() { return driverInfo; }
    public void setDriverInfo(String driverInfo) { this.driverInfo = driverInfo; }

    public String getRideStatus() { return rideStatus; }
    public void setRideStatus(String rideStatus) { this.rideStatus = rideStatus; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
