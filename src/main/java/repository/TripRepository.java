package repository;

import model.TripDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TripRepository extends MongoRepository<TripDocument, String> {
    TripDocument findByTripId(String tripId);
}
