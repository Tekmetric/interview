package com.interview.data.repository;

import com.interview.data.entity.ListItemEntity;
import com.interview.data.entity.ShoppingListEntity;
import com.interview.data.repository.jpa.ListItemJpaRepository;
import com.interview.data.repository.jpa.ShoppingListJpaRepository;
import com.interview.model.ListItem;
import com.interview.model.ShoppingList;
import com.interview.model.request.ListItemRequest;
import com.interview.model.request.ShoppingListRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class ShoppingListRepository {
    private final ShoppingListJpaRepository shoppingListJpaRepository;
    private final ListItemJpaRepository listItemJpaRepository;

    public ShoppingListRepository(ShoppingListJpaRepository shoppingListJpaRepository, ListItemJpaRepository listItemJpaRepository) {
        this.shoppingListJpaRepository = shoppingListJpaRepository;
        this.listItemJpaRepository = listItemJpaRepository;
    }

    public ShoppingList create(Long userId, ShoppingListRequest shoppingList) {
        ShoppingListEntity shoppingListEntity = shoppingListJpaRepository.save(new ShoppingListEntity(userId, shoppingList));
        // List<ListItemEntity> listItemEntities = listItemJpaRepository.saveAll(shoppingList.getListItems().stream().map(ListItemEntity::new).toList());

        return new ShoppingList(shoppingListEntity.getId(), shoppingListEntity.getStoreName(), List.of()
                // listItemEntities.stream().map(li->new ListItem(li.getId(), li.getItemName(), li.getChecked())).toList()
        );
    }

    public ShoppingList getById(Long shoppingListId) {
        ShoppingListEntity shoppingListEntity = shoppingListJpaRepository.getById(shoppingListId);

        List<ListItemEntity> listItemEntities = listItemJpaRepository.findByShoppingListId(shoppingListId);

        return toShoppingList(shoppingListEntity, listItemEntities);
    }

    private List<ListItem> toListItems(List<ListItemEntity> listItemEntities) {
        return listItemEntities.stream().map(this::toListItem).toList();
    }

    private ListItem toListItem(ListItemEntity listItemEntity) {
        return new ListItem(listItemEntity.getId(), listItemEntity.getItemName(), listItemEntity.getChecked());
    }

    public List<ShoppingList> getByUserId(Long userId) {
        List<ShoppingListEntity> shoppingListEntity = shoppingListJpaRepository.findByUserId(userId);

        return shoppingListEntity.stream().map( it->
                toShoppingList(it, listItemJpaRepository.findByShoppingListId(it.getId()))
        ).toList();


    }

    private ShoppingList toShoppingList(ShoppingListEntity shoppingListEntity, List<ListItemEntity> listItemEntities) {
        return new ShoppingList(shoppingListEntity.getId(), shoppingListEntity.getStoreName(), toListItems(listItemEntities));
    }

    @Transactional
    public void deleteByUserId(Long userId) {
        List<ShoppingListEntity> shoppingListEntities = shoppingListJpaRepository.findByUserId(userId);

        shoppingListEntities.forEach(shoppingListEntity-> {
            listItemJpaRepository.deleteByShoppingListId(shoppingListEntity.getId());
        });

        shoppingListJpaRepository.deleteAll(shoppingListEntities);
    }

    @Transactional
    public void deleteById(Long shoppingListId) {
        listItemJpaRepository.deleteByShoppingListId(shoppingListId);
        shoppingListJpaRepository.deleteById(shoppingListId);
    }

    @Transactional
    public ListItem createListItem(Long shoppingListId, ListItemRequest listItemRequest) {
        return toListItem(listItemJpaRepository.save(new ListItemEntity(shoppingListId, listItemRequest)));
    }

    public ListItem updateListItemChecked(Long listItemId, boolean isChecked) {
        ListItemEntity listItemEntity = listItemJpaRepository.getById(listItemId);

        listItemEntity.setChecked(isChecked);
        return toListItem(listItemJpaRepository.save(listItemEntity));
    }
}
