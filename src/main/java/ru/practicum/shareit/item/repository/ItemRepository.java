package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerIdOrderByIdAsc(Long ownerId);

    Page<Item> findAllByOwnerIdOrderByIdAsc(Long ownerId, Pageable page);

    List<Item> findByRequestRequesterId(Long requestId);

    @Query("SELECT i FROM Item i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(i.description) LIKE LOWER(CONCAT('%', :name, '%')) AND i.available = true")
    Page<Item> search(String name, Pageable pageable);

}
