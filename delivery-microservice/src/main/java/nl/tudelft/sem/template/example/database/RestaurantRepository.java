package nl.tudelft.sem.template.example.database;

import nl.tudelft.sem.template.example.entities.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, String> {}
