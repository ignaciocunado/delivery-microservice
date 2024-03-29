package nl.tudelft.sem.template.example.testRepositories;

import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TestDeliveryRepository implements DeliveryRepository {
    List<Delivery> list = new ArrayList<>();

    @Override
    public List<Delivery> findAll() {
        return list;
    }

    @Override
    public List<Delivery> findAll(Sort sort) {
        return list;
    }

    @Override
    public Page<Delivery> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Delivery> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Delivery> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Delivery> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public List<Delivery> findAllById(Iterable<UUID> ids) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public <S extends Delivery> long count(Example<S> example) {
        return 0;
    }

    @Override
    public void deleteById(UUID s) {

    }

    @Override
    public void delete(Delivery entity) {

    }

    @Override
    public void deleteAll(Iterable<? extends Delivery> entities) {
    }

    @Override
    public void deleteAll() {
        list.clear();
    }

    @Override
    public <S extends Delivery> S save(S entity) {
        for (Delivery d : list) {
            if (d.getDeliveryID().equals(entity.getDeliveryID())) {
                list.remove(d);
                break;
            }
        }
        list.add(entity);
        return entity;
    }

    @Override
    public <S extends Delivery> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<Delivery> findById(UUID s) {
        // find in list
        for (Delivery d : list) {
            if (d.getDeliveryID().equals(s)) {
                return Optional.of(d);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean existsById(UUID s) {
        for (Delivery d : list) {
            if (d.getDeliveryID().equals(s)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Delivery> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public void deleteInBatch(Iterable<Delivery> entities) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Delivery getOne(UUID s) {
        return null;
    }

    @Override
    public <S extends Delivery> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Delivery> boolean exists(Example<S> example) {
        return false;
    }
}
