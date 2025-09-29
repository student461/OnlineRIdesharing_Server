package manager;

import model.Driver; // Correct import for Driver class
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DriverMgr {
    private static DriverMgr driverMgrInstance;
    private final Map<String, Driver> driversMap = new ConcurrentHashMap<>();

    // Private constructor for Singleton pattern
    private DriverMgr() {}

    // Singleton instance getter
    public static DriverMgr getInstance() {
        if (driverMgrInstance == null) {
            synchronized (DriverMgr.class) {
                if (driverMgrInstance == null) {
                    driverMgrInstance = new DriverMgr();
                }
            }
        }
        return driverMgrInstance;
    }

    // Method to add a driver
    public void addDriver(String driverName, Driver driver) {
        driversMap.put(driverName, driver);
    }

    // Method to get the map of drivers
    public Map<String, Driver> getDriversMap() {
        return driversMap;
    }
}
