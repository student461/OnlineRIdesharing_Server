package strategy;
import java.util.List;
import java.util.stream.Collectors;

import common.VEHICLE_TYPE;
import model.*;

public class DriverPreFilter {
	public static List<Driver> filterByVehicleType(List<Driver> drivers, VEHICLE_TYPE type) {
	    return drivers.stream()
	        .filter(d -> d.getVehicleType() == type) // Enum comparison
	        .collect(Collectors.toList());
	}
}

