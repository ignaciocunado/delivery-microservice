package nl.tudelft.sem.template.example.testRepositories;

import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.stream.Collectors;

public class TestRestaurantRepository implements RestaurantRepository {
    Set<Restaurant> list = new HashSet<>();

    @Override
    public List<Restaurant> findAll() {
        return new ArrayList<>(list);
    }

    @Override
    public List<Restaurant> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<Restaurant> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Restaurant> List<S> findAll(Example<S> example) {
        return (List<S>) list;
    }

    @Override
    public <S extends Restaurant> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Restaurant> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public List<Restaurant> findAllById(Iterable<UUID> ids) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public <S extends Restaurant> long count(Example<S> example) {
        return 0;
    }

    @Override
    public void deleteById(UUID s) {

    }

    @Override
    public void delete(Restaurant entity) {

    }

    @Override
    public void deleteAll(Iterable<? extends Restaurant> entities) {

    }

    @Override
    public void deleteAll() {
        list = new HashSet<>();
    }

    @Override
    public <S extends Restaurant> S save(S entity) {
        if (entity.getRestaurantID() == null) {
            entity.setRestaurantID(UUID.randomUUID());
        }
        if (existsById(entity.getRestaurantID())) {
            list.removeIf(x -> x.getRestaurantID().equals(entity.getRestaurantID()));
        }
        list.add(entity);
        return entity;
    }

    @Override
    public <S extends Restaurant> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<Restaurant> findById(UUID s) {
        for (Restaurant restaurant : list) {
            if (restaurant.getRestaurantID().equals(s)) {
                return Optional.of(restaurant);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean existsById(UUID s) {
        return list.stream().anyMatch(x -> x.getRestaurantID().equals(s));
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Restaurant> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public void deleteInBatch(Iterable<Restaurant> entities) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Restaurant getOne(UUID s) {
        return null;
    }

    @Override
    public <S extends Restaurant> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Restaurant> boolean exists(Example<S> example) {
        return false;
    }
}
