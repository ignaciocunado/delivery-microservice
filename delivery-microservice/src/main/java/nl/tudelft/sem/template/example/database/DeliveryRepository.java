package nl.tudelft.sem.template.example.database;

import nl.tudelft.sem.template.example.entities.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, String> {}
