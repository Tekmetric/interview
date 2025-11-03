package com.interview;

import com.interview.data.entity.ListItemEntity;
import com.interview.data.entity.ShoppingListEntity;
import com.interview.data.repository.ShoppingListRepository;
import com.interview.data.repository.jpa.ListItemJpaRepository;
import com.interview.data.repository.jpa.ShoppingListJpaRepository;
import com.interview.model.ListItem;
import com.interview.model.ShoppingList;
import com.interview.model.request.ListItemRequest;
import com.interview.model.request.ShoppingListRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import(ShoppingListRepository.class)
@TestPropertySource(locations = "classpath:application.properties")
class ShoppingListRepositoryTest {

    @Autowired
    private ShoppingListRepository shoppingListRepository;

    @Autowired
    private ShoppingListJpaRepository shoppingListJpaRepository;

    @Autowired
    private ListItemJpaRepository listItemJpaRepository;

    @BeforeEach
    void setUp() {
        listItemJpaRepository.deleteAll();
        shoppingListJpaRepository.deleteAll();
    }

    @Test
    void testCreateShoppingList() {
        Long userId = 1L;
        String storeName = "Jack's Grocery";
        ShoppingListRequest request = new ShoppingListRequest(storeName);

        ShoppingList result = shoppingListRepository.create(userId, request);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(storeName, result.getStoreName());
        assertTrue(result.getListItems().isEmpty());
    }

    @Test
    void testGetShoppingListById() {
        Long userId = 1L;
        String storeName = "Hardware Store";
        ShoppingListEntity entity = new ShoppingListEntity(null, userId, storeName);
        ShoppingListEntity saved = shoppingListJpaRepository.save(entity);

        ListItemEntity item1 = new ListItemEntity(null, saved.getId(), "Milk", false);
        ListItemEntity item2 = new ListItemEntity(null, saved.getId(), "Bread", true);
        listItemJpaRepository.saveAll(List.of(item1, item2));

        ShoppingList result = shoppingListRepository.getById(saved.getId());

        assertNotNull(result);
        assertEquals(saved.getId(), result.getId());
        assertEquals(storeName, result.getStoreName());
        assertEquals(2, result.getListItems().size());

        Set<String> itemNames = getListItemNames(result.getListItems());
        assertEquals(Set.of("Milk", "Bread"), itemNames);
    }

    @Test
    void testGetShoppingListsByUserId() {
        Long userId = 1L;
        String storeName1 = "Jack's Grocery";
        String storeName2 = "Hardware";
        ShoppingListEntity list1 = shoppingListJpaRepository.save(new ShoppingListEntity(null, userId, storeName1));
        ShoppingListEntity list2 = shoppingListJpaRepository.save(new ShoppingListEntity(null, userId, storeName2));
        ShoppingListEntity list3 = shoppingListJpaRepository.save(new ShoppingListEntity(null, 2L, "Costco"));

        listItemJpaRepository.save(new ListItemEntity(null, list1.getId(), "Apples", false));
        listItemJpaRepository.save(new ListItemEntity(null, list2.getId(), "Oranges", true));

        List<ShoppingList> results = shoppingListRepository.getByUserId(userId);

        assertEquals(2, results.size());

        Set<String> storeNames = getStoreNames(results);
        assertEquals(Set.of(storeName1, storeName2), storeNames);
    }

    @Test
    void testDeleteShoppingListById() {
        Long userId = 1L;
        ShoppingListEntity list = shoppingListJpaRepository.save(new ShoppingListEntity(null, userId, "Walmart"));
        listItemJpaRepository.save(new ListItemEntity(null, list.getId(), "Item1", false));
        listItemJpaRepository.save(new ListItemEntity(null, list.getId(), "Item2", false));

        shoppingListRepository.deleteById(list.getId());

        assertTrue(shoppingListJpaRepository.findById(list.getId()).isEmpty());
        assertTrue(listItemJpaRepository.findByShoppingListId(list.getId()).isEmpty());
    }

    @Test
    void testDeleteShoppingListsByUserId() {
        Long userId = 1L;
        ShoppingListEntity list1 = shoppingListJpaRepository.save(new ShoppingListEntity(null, userId, "Walmart"));
        ShoppingListEntity list2 = shoppingListJpaRepository.save(new ShoppingListEntity(null, userId, "Target"));
        ShoppingListEntity list3 = shoppingListJpaRepository.save(new ShoppingListEntity(null, 2L, "Costco"));

        listItemJpaRepository.save(new ListItemEntity(null, list1.getId(), "Item1", false));
        listItemJpaRepository.save(new ListItemEntity(null, list2.getId(), "Item2", false));
        listItemJpaRepository.save(new ListItemEntity(null, list3.getId(), "Item3", false));

        shoppingListRepository.deleteByUserId(userId);

        assertTrue(shoppingListJpaRepository.findById(list1.getId()).isEmpty());
        assertTrue(shoppingListJpaRepository.findById(list2.getId()).isEmpty());
        assertTrue(shoppingListJpaRepository.findById(list3.getId()).isPresent());
        assertTrue(listItemJpaRepository.findByShoppingListId(list1.getId()).isEmpty());
        assertTrue(listItemJpaRepository.findByShoppingListId(list2.getId()).isEmpty());
        assertEquals(1, listItemJpaRepository.findByShoppingListId(list3.getId()).size());
    }

    @Test
    void testCreateListItem() {
        Long userId = 1L;
        ShoppingListEntity list = shoppingListJpaRepository.save(new ShoppingListEntity(null, userId, "Walmart"));
        ListItemRequest request = new ListItemRequest("Milk", false);

        ListItem result = shoppingListRepository.createListItem(list.getId(), request);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Milk", result.getItemName());
        assertFalse(result.getChecked());
    }

    @Test
    void testUpdateListItemChecked() {
        Long userId = 1L;
        ShoppingListEntity list = shoppingListJpaRepository.save(new ShoppingListEntity(null, userId, "Walmart"));
        ListItemEntity item = listItemJpaRepository.save(new ListItemEntity(null, list.getId(), "Milk", false));

        ListItem result = shoppingListRepository.updateListItemChecked(item.getId(), true);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertTrue(result.getChecked());
        assertEquals("Milk", result.getItemName());
    }

    @Test
    void testReturnEmptyListWhenUserHasNoShoppingLists() {
        List<ShoppingList> results = shoppingListRepository.getByUserId(999L);

        assertTrue(results.isEmpty());
    }

    @Test
    void testReturnShoppingListWithEmptyItems() {
        Long userId = 1L;
        ShoppingListEntity list = shoppingListJpaRepository.save(new ShoppingListEntity(null, userId, "Walmart"));

        ShoppingList result = shoppingListRepository.getById(list.getId());

        assertNotNull(result);
        assertTrue(result.getListItems().isEmpty());
    }

    Set<String> getListItemNames(List<ListItem> listItems) {
        return listItems.stream()
                .map(ListItem::getItemName)
                .collect(Collectors.toSet());
    }

    Set<String> getStoreNames(List<ShoppingList> shoppingLists) {
        return shoppingLists.stream()
                .map(ShoppingList::getStoreName)
                .collect(Collectors.toSet());
    }
}
