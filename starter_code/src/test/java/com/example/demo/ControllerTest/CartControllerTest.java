package com.example.demo.ControllerTest;

import com.example.demo.TestUtils;
import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CartControllerTest {
    private CartController cartController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private ItemRepository itemRepository = mock(ItemRepository.class);


    private static  final String MOCK_USERNAME ="thien";

    private static  final String MOC_INVALID_USERNAME ="thien2";

    private static  final Long MOCK_ITEM_ID = 1L;

    private  static  final int MOCK_QUANTITY =1;

    private static final String MOCK_PRICE ="123.2";

    @Before
    public void setUP(){
        cartController = new CartController();

        TestUtils.injectObject(cartController,"userRepository",userRepository);
        TestUtils.injectObject(cartController,"cartRepository",cartRepository);
        TestUtils.injectObject(cartController,"itemRepository",itemRepository);

        when(userRepository.findByUsername(MOCK_USERNAME)).thenReturn(getUser());
        when(itemRepository.findById(MOCK_ITEM_ID)).thenReturn(getItem());
    }
    @Test
    public void testAddItemToCart(){
        int expectQuantity = MOCK_QUANTITY+1;
        BigDecimal expectdToeal = new BigDecimal(MOCK_PRICE).multiply(BigDecimal.valueOf(expectQuantity));

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(MOCK_ITEM_ID);
        modifyCartRequest.setUsername(MOCK_USERNAME);
        modifyCartRequest.setQuantity(MOCK_QUANTITY);

        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
        Cart cart = response.getBody();

        assertEquals(HttpStatus.OK.value(),response.getStatusCodeValue());
        assertNotNull(cart);
        assertEquals(MOCK_USERNAME,cart.getUser().getUsername());
        assertEquals(expectQuantity,cart.getItems().size());
        assertEquals(expectdToeal,cart.getTotal());
    }
    @Test
    public void testInvalidUsername(){
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(MOC_INVALID_USERNAME);
        modifyCartRequest.setItemId(MOCK_ITEM_ID);
        modifyCartRequest.setQuantity(MOCK_QUANTITY);

        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
        assertEquals(HttpStatus.NOT_FOUND.value(),response.getStatusCodeValue());
    }
    @Test
    public void testInvalidItem(){
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(MOC_INVALID_USERNAME);
        modifyCartRequest.setItemId(0L);
        modifyCartRequest.setQuantity(MOCK_QUANTITY);

        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
        assertEquals(HttpStatus.NOT_FOUND.value(),response.getStatusCodeValue());
    }


    @Test
    public void removeItemInCart(){
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(MOCK_USERNAME);
        modifyCartRequest.setItemId(MOCK_ITEM_ID);
        modifyCartRequest.setQuantity(1);

        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
        Cart cart = response.getBody();
        assertEquals(HttpStatus.OK.value(),response.getStatusCodeValue());
        assertNotNull(cart);
        assertEquals(MOCK_USERNAME,cart.getUser().getUsername());
        assertEquals(0,cart.getItems().size());
        assertEquals(0,cart.getTotal().intValue());

    }

    private static User getUser() {
        User user = new User();
        user.setUsername(MOCK_USERNAME);
        user.setCart(getCart(user));
        return user;
    }

    private static Cart getCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        cart.addItem(getItem().orElse(null));
        return cart;
    }

    private static Optional<Item> getItem() {
        Item item = new Item();
        item.setId(MOCK_ITEM_ID);
        item.setPrice(new BigDecimal(MOCK_PRICE));
        return Optional.of(item);
    }
}
