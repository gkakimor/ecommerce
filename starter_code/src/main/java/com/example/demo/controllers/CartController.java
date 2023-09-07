package com.example.demo.controllers;

import java.util.Optional;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

@RestController
@RequestMapping("/api/cart")
public class CartController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private ItemRepository itemRepository;

	private static final Logger log = LogManager.getLogger(CartController.class);
	
	@PostMapping("/addToCart")
	public ResponseEntity<Cart> addTocart(@RequestBody ModifyCartRequest request) {
		log.info("CartController: addTocart execution STARTED.");
		log.info("CartController: addTocart execution. Searching user " + request.getUsername());
		User user = userRepository.findByUsername(request.getUsername());
		if(user == null) {
			log.error("CartController: addTocart execution. User " + request.getUsername() + " not found.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		log.info("CartController: addTocart execution. Searching item ID " + request.getItemId());
		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {
			log.error("CartController: addTocart execution. Item not found.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		log.info("CartController: addTocart execution. Adding item to cart");
		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.addItem(item.get()));
		cartRepository.save(cart);

		log.info("CartController: addTocart execution FINISHED.");
		return ResponseEntity.ok(cart);
	}
	
	@PostMapping("/removeFromCart")
	public ResponseEntity<Cart> removeFromcart(@RequestBody ModifyCartRequest request) {
		log.info("CartController: removeFromcart execution STARTED.");
		log.info("CartController: removeFromcart execution. Searching user " + request.getUsername());
		User user = userRepository.findByUsername(request.getUsername());
		if(user == null) {
			log.error("CartController: removeFromcart execution. User " + request.getUsername() + " not found.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		log.info("CartController: removeFromcart execution. Searching item ID " + request.getItemId());
		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {
			log.error("CartController: removeFromcart execution. Item not found.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		log.info("CartController: removeFromcart execution. Removing item from cart");
		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.removeItem(item.get()));
		cartRepository.save(cart);

		log.info("CartController: removeFromcart execution FINISHED.");
		return ResponseEntity.ok(cart);
	}
		
}