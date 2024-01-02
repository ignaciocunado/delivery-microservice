package nl.tudelft.sem.template.example.database;

import nl.tudelft.sem.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {}
