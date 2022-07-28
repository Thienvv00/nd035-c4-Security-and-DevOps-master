package com.example.demo.ControllerTest;

import com.example.demo.TestUtils;
import com.example.demo.controllers.CartController;
import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;
    private ItemRepository itemRepo = mock(ItemRepository.class);


    @Before
    public void setUp(){
        itemController = new ItemController();
        TestUtils.injectObject(itemController,"itemRepository",itemRepo);

        Item item1 = getItem(1L, "item 01");
        Item item2 = getItem(2L, "item 02 dup");
        Item item3 = getItem(3L, "item 02 dup");
        Item item4 = getItem(4L, "item 03");

        //find item by id -> return single item
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item1));
        //find item by name, some item have the same name -> return list
        when(itemRepo.findByName(item2.getName())).thenReturn(Lists.list(item2, item3));
        //return all item
        when(itemRepo.findAll()).thenReturn(Lists.list(item1, item2, item3, item4));
    }
    @Test
    public void testFindItemByID(){
        Long mockID = 1L;
        ResponseEntity<Item> response = itemController.getItemById(mockID);
        Item item = response.getBody();
        assertNotNull(item);
        assertEquals(HttpStatus.OK.value(),response.getStatusCodeValue());
    }
    @Test
    public void testFindItemByName(){
        String  mockName = "item 02 dup";
        ResponseEntity<List<Item>> response = itemController.getItemsByName(mockName);
        List<Item> item = response.getBody();
        assertNotNull(item);
        assertEquals(2 ,item.size());
        assertEquals(HttpStatus.OK.value(),response.getStatusCodeValue());
    }
    @Test
    public void testFindAllItem(){

        ResponseEntity<List<Item>> response = itemController.getItems();
        List<Item> item = response.getBody();
        assertNotNull(item);
        assertEquals(4 ,item.size());
        assertEquals(HttpStatus.OK.value(),response.getStatusCodeValue());
    }
    @Test
    public void findItemNotExistId(){
        Long mockID = 5L;
        ResponseEntity<Item> response = itemController.getItemById(mockID);
        Item item = response.getBody();
        assertNull(item);
        assertEquals(HttpStatus.NOT_FOUND.value(),response.getStatusCodeValue());

    }
    @Test
    public void findItemNotExistName(){
        String  mockName = "item 05 dup";
        ResponseEntity<List<Item>> response = itemController.getItemsByName(mockName);
        List<Item> item = response.getBody();
        assertNull(item);
        assertEquals(HttpStatus.NOT_FOUND.value(),response.getStatusCodeValue());

    }
    private static Item getItem(long itemId, String itemName) {
        Item item = new Item();
        item.setId(itemId);
        item.setName(itemName);
        return item;
    }

}
