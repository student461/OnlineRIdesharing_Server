package main;


public class  TripRequest {

    private String name;
    private String id;
    private  double fromlat;
    private  double fromlon;
    private  double tolat;
    private  double tolon;
    public String preference; 
    public String Vehiclepreference;
    private double duration;
    private double distance;
    
 

public TripRequest(String name, String id, double fromlat, double fromlon, double tolat, double tolon,
			String preference, String vehiclepreference, double duration, double distance) {
		super();
		this.name = name;
		this.id = id;
		this.fromlat = fromlat;
		this.fromlon = fromlon;
		this.tolat = tolat;
		this.tolon = tolon;
		this.preference = preference;
		Vehiclepreference = vehiclepreference;
		this.duration = duration;
		this.distance = distance;
	}





public String getVehiclepreference() {
		return Vehiclepreference;
	}


	public void setVehiclepreference(String vehiclepreference) {
		Vehiclepreference = vehiclepreference;
	}


public String getName() {
      return name;
  }
  
  public void setName(String name) {
      this.name = name;
  }
  
  public String getPreference() {
      return preference;
  }
  
  public void setPreference(String preference) {
      this.preference = preference;
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

public double getTolat() {
	return tolat;
}

public void setTolat(double tolat) {
	this.tolat = tolat;
}

public double getTolon() {
	return tolon;
}

public void setTolon(double tolon) {
	this.tolon = tolon;
}
public double getDuration() {
		return duration;
	}


	public void setDuration(double duration) {
		this.duration = duration;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public TripRequest() {
 }

  
  }