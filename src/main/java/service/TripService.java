package service;

import model.Trip;
import model.TripDocument;
import repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;

    public TripDocument createTrip(Trip trip) {
        TripDocument document = new TripDocument(trip);
        return tripRepository.save(document);
    }

    public TripDocument updateTrip(Trip trip) {
        TripDocument existing = tripRepository.findByTripId(trip.getTripId());
        if (existing != null) {
            TripDocument updated = new TripDocument(trip);
            updated.setId(existing.getId());
            return tripRepository.save(updated);
        }
        return null;
    }
}
