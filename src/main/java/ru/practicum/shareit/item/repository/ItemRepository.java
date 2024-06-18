package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    @Query(value = "SELECT * FROM items AS items WHERE items.is_available = TRUE AND (items.name ILIKE %?1% OR items.description ILIKE %?1%)", nativeQuery = true)
    List<Item> getUsersItemsForSearch(String search);

    List<Item> findByOwnerIdEquals(int id);
}