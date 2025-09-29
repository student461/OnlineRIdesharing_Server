package model;

import java.time.LocalDateTime;

import common.RATING;
import common.VEHICLE_TYPE;;

public class TripMetaData {
    private final Location srcLoc;
    private final Location dstLoc;
    private final RATING riderRating;
    private RATING driverRating;
	private double distanceKm;
	private double durationMinutes;
	private VEHICLE_TYPE vehicleType;
	private LocalDateTime tripTime;
	private  boolean isPeakHour;
	private String cityCode;

    public TripMetaData(Location srcLoc, Location dstLoc, RATING riderRating, 
            VEHICLE_TYPE vehicleType, double distanceKm, 
            double durationMinutes, LocalDateTime tripTime) {
this.srcLoc = srcLoc;
this.dstLoc = dstLoc;
this.riderRating = riderRating;
this.driverRating = RATING.UNASSIGNED;
this.vehicleType = vehicleType;
this.distanceKm = distanceKm;
this.durationMinutes = durationMinutes;
this.tripTime = tripTime;
this.isPeakHour = calculatePeakHour(tripTime);
this.cityCode = getCityCode(srcLoc); // Geocode-based
}

    private boolean calculatePeakHour(LocalDateTime time) {
        int hour = time.getHour();
        return (hour >= 7 && hour <= 10) || (hour >= 17 && hour <= 20); // AM/PM peaks
    }

    private String getCityCode(Location loc) {
        // Call a geocoding service or use a local DB
        return "CHN"; // Example: Chennai
    }
    public double getDistanceKm() {
		return distanceKm;
	}

	public void setDistanceKm(double distanceKm) {
		this.distanceKm = distanceKm;
	}

	public double getDurationMinutes() {
		return durationMinutes;
	}

	public void setDurationMinutes(double durationMinutes) {
		this.durationMinutes = durationMinutes;
	}

	public VEHICLE_TYPE getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(VEHICLE_TYPE vehicleType) {
		this.vehicleType = vehicleType;
	}

	public LocalDateTime getTripTime() {
		return tripTime;
	}

	public void setTripTime(LocalDateTime tripTime) {
		this.tripTime = tripTime;
	}

	public Object getIsPeakHour() {
		return isPeakHour;
	}

	public void setIsPeakHour(Boolean isPeakHour) {
		this.isPeakHour = isPeakHour;
	}

	public Object getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public RATING getDriverRating() {
		return driverRating;
	}

	public Location getSrcLoc() {
        return srcLoc;
    }

    public Location getDstLoc() {
        return dstLoc;
    }

    public RATING getRiderRating() {
        return riderRating;
    }

 

    public void setDriverRating(RATING driverRating) {
        this.driverRating = driverRating;
    }
    public double getDriverRatingValue() {
        return driverRating.getValue();
    }
    
}
