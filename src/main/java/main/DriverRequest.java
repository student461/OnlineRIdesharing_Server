package main;

public class DriverRequest {

    private String name;
    private String id;
    private  double fromlat;
    private  double fromlon;
    private double rating;
    
	public DriverRequest() {
    }
  
	public DriverRequest(String name, String id, double fromlat, double fromlon, double rating) {
		super();
		this.name = name;
		this.id = id;
		this.fromlat = fromlat;
		this.fromlon = fromlon;
		this.rating = rating;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public double getFromlat() {
		return fromlat;
	}
	public void setFromlat(double fromlat) {
		this.fromlat = fromlat;
	}
	public double getFromlon() {
		return fromlon;
	}
	public void setFromlon(double fromlon) {
		this.fromlon = fromlon;
	}
	public double getRating() {
		return rating;
	}
	public void setRating(double rating) {
		this.rating = rating;
	}
}