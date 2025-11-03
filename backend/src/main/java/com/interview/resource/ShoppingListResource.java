package com.interview.resource;

import com.interview.data.repository.ShoppingListRepository;
import com.interview.model.ListItem;
import com.interview.model.ShoppingList;
import com.interview.model.request.ListItemRequest;
import com.interview.model.request.ShoppingListRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shopping_list")
public class ShoppingListResource {
    private ShoppingListRepository shoppingListRepository;

    @Autowired
    public ShoppingListResource(ShoppingListRepository shoppingListRepository) {
        this.shoppingListRepository = shoppingListRepository;
    }
    @PostMapping("/user/{userId}")
    public ShoppingList createShoppingList(@PathVariable("userId") Long userId, @RequestBody ShoppingListRequest shoppingList) {
        return shoppingListRepository.create(userId, shoppingList);
    }

    @GetMapping("/{shoppingListId}")
    public ShoppingList getShoppingList(@PathVariable("shoppingListId") Long shoppingListId) {
        return shoppingListRepository.getById(shoppingListId);
    }

    @DeleteMapping("/{shoppingListId}")
    public void deleteShoppingList(@PathVariable("shoppingListId") Long shoppingListId) {
        shoppingListRepository.deleteById(shoppingListId);
    }

    @PostMapping("/{shoppingListId}/item")
    public ListItem createShoppingListItem(@PathVariable("shoppingListId") Long shoppingListId, @RequestBody ListItemRequest listItemRequest) {
        return shoppingListRepository.createListItem(shoppingListId, listItemRequest);
    }

    @PatchMapping("/item/{itemId}/check")
    public ListItem checkShoppingListItem(@PathVariable("itemId") Long itemId) {
        return shoppingListRepository.updateListItemChecked(itemId, true);
    }

    @PatchMapping("/item/{itemId}/uncheck")
    public ListItem uncheckShoppingListItem(@PathVariable("itemId") Long itemId) {
        return shoppingListRepository.updateListItemChecked(itemId, false);
    }
}
