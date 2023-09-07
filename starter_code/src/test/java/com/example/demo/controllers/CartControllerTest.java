package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;

    private UserRepository userRepository = mock(UserRepository.class);
    
    private CartRepository cartRepository = mock(CartRepository.class);

    private ItemRepository itemRepository = mock(ItemRepository.class);
    
    @Before
    public void setUp(){

        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }
    
    @Test
    public void add_to_cart_happy_path() throws Exception {
        User user = createUser("test", "thisIsHashed");
        when(userRepository.findByUsername("test")).thenReturn(user);

        Item item = createItem();
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(user.getUsername());
        request.setItemId(1L);
        request.setQuantity(1);

        final ResponseEntity<Cart> response = cartController.addTocart(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(1, cart.getItems().size());
        assertEquals("Round Widget", cart.getItems().get(0).getName());
        assertEquals("A widget that is round", cart.getItems().get(0).getDescription());
    }

    @Test
    public void remove_from_cart_happy_path() throws Exception {
        User user = createUser("test", "thisIsHashed");
        when(userRepository.findByUsername("test")).thenReturn(user);
        user = addItemToCart(user);

        Item item = createItem();
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(user.getUsername());
        request.setItemId(1L);
        request.setQuantity(1);

        final ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(1, cart.getItems().size());
        assertEquals("A widget that is round", cart.getItems().get(0).getDescription());
    }

    public User createUser(String username, String password){
        Cart cart = new Cart();
        cart.setId(1L);
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setCart(cart);
        return user;
    }

    public Item createItem ()
    {
        Item item = new Item();
        item.setId(1L);
        item.setName("Round Widget");
        item.setPrice(new BigDecimal(2.99));
        item.setDescription("A widget that is round");
        return item;
    }

    public User addItemToCart(User user)
    {
        Item item = createItem();
        user.getCart().setItems(new ArrayList<Item>());
        user.getCart().getItems().add(item);
        user.getCart().getItems().add(item);

        return user;
    }

}
