package com.interview.data.entity;

import com.interview.model.ListItem;
import com.interview.model.request.ListItemRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "list_item")
public class ListItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long shoppingListId;
    private String itemName;
    private Boolean checked;

    public ListItemEntity(ListItem listItem) {
        this.id = listItem.getId();
        this.itemName = listItem.getItemName();
        this.checked = listItem.getChecked();
    }

    public ListItemEntity(Long shoppingListId, ListItemRequest listItemRequest) {
        this.shoppingListId = shoppingListId;
        this.itemName = listItemRequest.getItemName();
        this.checked = listItemRequest.getChecked();
    }
}
