package manager;


import model.Rider;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RiderMgr {
    private static RiderMgr riderMgrInstance;
    private final Map<String, Rider> ridersMap = new ConcurrentHashMap<>();

    private RiderMgr() {}

    public static RiderMgr getInstance() {
        if (riderMgrInstance == null) {
            synchronized (RiderMgr.class) {
                if (riderMgrInstance == null) {
                    riderMgrInstance = new RiderMgr();
                }
            }
        }
        return riderMgrInstance;
    }

    public void addRider(String riderName, Rider rider) {
        ridersMap.put(riderName, rider);
    }
      public Collection<Rider> getAllRiders() {
        return ridersMap.values();
    }

    public Rider getRider(String name) {
        return ridersMap.get(name);
    }
    
}
