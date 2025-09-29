package common;

public enum VEHICLE_TYPE {
    BIKE(30, 8, 0.5),   // Base: ₹30, ₹8/km, ₹0.5/min
    AUTO(50, 10, 1),    // Base: ₹50, ₹10/km, ₹1/min
    CAR(100, 15, 1.5);  // Base: ₹100, ₹15/km, ₹1.5/min

    private final double baseFare;
    private final double ratePerKm;
    private final double ratePerMinute;

    VEHICLE_TYPE(double baseFare, double ratePerKm, double ratePerMinute) {
        this.baseFare = baseFare;
        this.ratePerKm = ratePerKm;
        this.ratePerMinute = ratePerMinute;
    }

    public double getBaseFare() {
        return baseFare;
    }

    public double getRatePerKm() {
        return ratePerKm;
    }

    public double getRatePerMinute() {
        return ratePerMinute;
    }

    // ✅ Static method to safely convert string to enum
    public static VEHICLE_TYPE fromValue(String vehicleChoice) {
        if (vehicleChoice == null) return null;
        try {
            return VEHICLE_TYPE.valueOf(vehicleChoice.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid vehicle type: " + vehicleChoice);
            return null; // or throw custom exception if needed
        }
    }
}
