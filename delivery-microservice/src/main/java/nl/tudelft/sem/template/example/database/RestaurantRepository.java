package nl.tudelft.sem.template.example.database;

import nl.tudelft.sem.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {}
