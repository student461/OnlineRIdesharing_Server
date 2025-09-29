
	package model;

	import common.TRIP_STATUS;
	import org.springframework.data.annotation.Id;
	import org.springframework.data.mongodb.core.mapping.Document;

	@Document(collection = "trips")
	public class TripDocument {

	    @Id
	    private String id;

	    private String tripId;
	    private Rider rider;
	    private Driver driver;
	    private Location srcLoc;
	    private Location dstLoc;
	    private double fare;
	    private double rating;
	    private TRIP_STATUS status;
	    private boolean driverAccepted;
	    private boolean riderEnded;
	    private boolean driverEnded;

	    // Constructors, getters and setters
	    public TripDocument() {}

	    public TripDocument(Trip trip) {
	        this.tripId = trip.getTripId();
	        this.rider = trip.getRider();
	        this.driver = trip.getDriver();
	        this.srcLoc = trip.getSrcLoc();
	        this.dstLoc = trip.getDstLoc();
	        this.fare = trip.getFare();
	        this.rating = trip.getRating();
	        this.status = trip.getStatus();
	        this.driverAccepted = trip.isDriverAccepted();
	        this.riderEnded = trip.isRiderEnded();
	        this.driverEnded = trip.isDriverEnded();
	    }

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getTripId() {
			return tripId;
		}

		public void setTripId(String tripId) {
			this.tripId = tripId;
		}

		public Rider getRider() {
			return rider;
		}

		public void setRider(Rider rider) {
			this.rider = rider;
		}

		public Driver getDriver() {
			return driver;
		}

		public void setDriver(Driver driver) {
			this.driver = driver;
		}

		public Location getSrcLoc() {
			return srcLoc;
		}

		public void setSrcLoc(Location srcLoc) {
			this.srcLoc = srcLoc;
		}

		public Location getDstLoc() {
			return dstLoc;
		}

		public void setDstLoc(Location dstLoc) {
			this.dstLoc = dstLoc;
		}

		public double getFare() {
			return fare;
		}

		public void setFare(double fare) {
			this.fare = fare;
		}

		public double getRating() {
			return rating;
		}

		public void setRating(double rating) {
			this.rating = rating;
		}

		public TRIP_STATUS getStatus() {
			return status;
		}

		public void setStatus(TRIP_STATUS status) {
			this.status = status;
		}

		public boolean isDriverAccepted() {
			return driverAccepted;
		}

		public void setDriverAccepted(boolean driverAccepted) {
			this.driverAccepted = driverAccepted;
		}

		public boolean isRiderEnded() {
			return riderEnded;
		}

		public void setRiderEnded(boolean riderEnded) {
			this.riderEnded = riderEnded;
		}

		public boolean isDriverEnded() {
			return driverEnded;
		}

		public void setDriverEnded(boolean driverEnded) {
			this.driverEnded = driverEnded;
		}

	    // Getters and setters ...
	}


