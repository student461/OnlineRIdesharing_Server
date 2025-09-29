package strategy;

import model.TripMetaData;

import java.util.List;
import java.util.Set;

import model.Driver;

public interface DriverMatchingStrategy {
    Driver matchDriver(List<Driver> driversMapLayer2,TripMetaData tripMetaData,Set<String> rejectedDriverIds);
}
