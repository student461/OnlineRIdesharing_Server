package repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import model.drivers;
import java.util.List;
import java.util.Optional;

public interface UserDriverRepository extends MongoRepository<drivers, String> {
    // Custom method to find all drivers by role
//	Optional<UserDriver> findByclerkId(String clerkId);
	@Query("{clerkId: ?0}")
	Optional<drivers> findByclerkId(String clerkId);


}
