package com.example.demo.ControllerTest;

import com.example.demo.TestUtils;
import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private OrderController orderController;
    private UserRepository userRepo = mock(UserRepository.class);
    private OrderRepository orderRepo = mock(OrderRepository.class);

    private static final String MOCK_USERNAME = "thien";
    private static final String MOCK_INVALID_USERNAME = "thien fake";
    private static final long MOCK_ITEM_ID = 1L;
    private static final String MOCK_PRICE = "12.34";
    @Before
    public void setUp(){
        orderController = new OrderController();
        TestUtils.injectObject(orderController,"userRepository",userRepo);
        TestUtils.injectObject(orderController,"orderRepository",orderRepo);

        when(userRepo.findByUsername(MOCK_USERNAME)).thenReturn(getUser());
        when(orderRepo.findByUser(any())).thenReturn(getUserOrders());
    }
    @Test
    public void testSubmitOrder(){
        ResponseEntity<UserOrder> response = orderController.submit(MOCK_USERNAME);
        UserOrder userOrder = response.getBody();
        assertNotNull(userOrder);
        assertEquals(200,response.getStatusCodeValue());
        assertEquals(MOCK_USERNAME,userOrder.getUser().getUsername());
        assertEquals(1,userOrder.getItems().size());
        assertEquals(new BigDecimal(MOCK_PRICE),userOrder.getTotal());

    }
    @Test
    public void GetOrderFOrUser(){
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(MOCK_USERNAME);
        List<UserOrder> userOrders = response.getBody();
        assertEquals(response.getStatusCodeValue(), HttpStatus.OK.value());
        assertNotNull(userOrders);
        assertEquals(userOrders.size(), 1);

        UserOrder userOrder = userOrders.get(0);
        assertEquals( MOCK_USERNAME,userOrder.getUser().getUsername());
        assertEquals(new BigDecimal(MOCK_PRICE),userOrder.getTotal());
        assertEquals(1,userOrder.getItems().size());
    }
    @Test
    public void testSubmitWithInvalidUsername() {
        ResponseEntity<UserOrder> response = orderController.submit(MOCK_INVALID_USERNAME);
        assertEquals(HttpStatus.NOT_FOUND.value(),response.getStatusCodeValue());
    }

    @Test
    public void testGetOrderWithInvalidUsername() {
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(MOCK_INVALID_USERNAME);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
    }
    private static User getUser() {
        User user = new User();
        user.setUsername(MOCK_USERNAME);
        user.setCart(getCart(user));
        return user;

    }
    private static Optional<Item> getItem() {
        Item item = new Item();
        item.setId(MOCK_ITEM_ID);
        item.setPrice(new BigDecimal(MOCK_PRICE));
        return Optional.of(item);
    }

    private static Cart getCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        cart.addItem(getItem().orElse(null));
        return cart;
    }

    private static List<UserOrder> getUserOrders() {
        UserOrder userOrder = UserOrder.createFromCart(getUser().getCart());
        return Lists.list(userOrder);
    }
}
