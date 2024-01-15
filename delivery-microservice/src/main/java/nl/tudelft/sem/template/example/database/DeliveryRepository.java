package nl.tudelft.sem.template.example.database;

import java.util.UUID;
import nl.tudelft.sem.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {}
