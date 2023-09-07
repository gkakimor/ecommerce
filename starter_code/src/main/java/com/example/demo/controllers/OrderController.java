package com.example.demo.controllers;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

@RestController
@RequestMapping("/api/order")
public class OrderController {
	
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrderRepository orderRepository;

	private static final Logger log = LogManager.getLogger(OrderController.class);
	
	
	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username) {
		log.info("OrderController: submit execution STARTED. Username: " + username);
		log.info("OrderController: submit execution. Searching user " + username);
		User user = userRepository.findByUsername(username);
		if(user == null) {
			log.error("OrderController: submit execution. User " + username + " not found.");
			return ResponseEntity.notFound().build();
		}
		log.info("OrderController: submit execution. Saving order.");
		UserOrder order = UserOrder.createFromCart(user.getCart());
		orderRepository.save(order);
		log.info("OrderController: submit execution FINISHED.");
		return ResponseEntity.ok(order);
	}
	
	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
		log.info("OrderController: getOrdersForUser execution STARTED. Username: " + username);
		log.info("OrderController: getOrdersForUser execution. Searching user " + username);
		User user = userRepository.findByUsername(username);
		if(user == null) {
			log.error("OrderController: getOrdersForUser execution. User " + username + " not found.");
			return ResponseEntity.notFound().build();
		}
		log.info("OrderController: getOrdersForUser execution FINISHED.");
		return ResponseEntity.ok(orderRepository.findByUser(user));
	}
}
