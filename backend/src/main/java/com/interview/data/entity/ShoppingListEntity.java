package com.interview.data.entity;

import com.interview.model.ShoppingList;
import com.interview.model.request.ShoppingListRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "shopping_list")
public class ShoppingListEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String storeName;

    public ShoppingListEntity(Long userId, ShoppingListRequest shoppingList) {
        this.userId = userId;
        this.storeName = shoppingList.getStoreName();
    }
}
