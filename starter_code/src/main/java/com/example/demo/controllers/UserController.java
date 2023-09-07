package com.example.demo.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

	private static final Logger log = LogManager.getLogger(UserController.class);
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		log.info("UserController: findById execution STARTED. User ID: " + id);
		Optional<User> newUser = userRepository.findById(id);
		log.info("UserController: findById execution FINISHED. User ID: " + id);
		return ResponseEntity.of(newUser);
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		log.info("UserController: findByUserName execution STARTED. Username: " + username);
		User user = userRepository.findByUsername(username);

		if (user == null) {
			log.error("UserController: findByUserName execution. User " + username + " not found.");
			return ResponseEntity.notFound().build();
		} else {
			log.info("UserController: findByUserName execution FINISHED. Username: " + username);
			return ResponseEntity.ok(user);
		}
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		log.info("UserController: createUser execution STARTED. Username: " + createUserRequest.getUsername());
		User user = new User();
		user.setUsername(createUserRequest.getUsername());

		Cart cart = new Cart();
		log.info("UserController: createUser execution. Saving cart.");
		cartRepository.save(cart);
		user.setCart(cart);
		if(createUserRequest.getPassword().length() < 7 ||
				!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())){
			//System.out.println("Error - Either length is less than 7 or pass and conf pass do not match. Unable to create user.");
			log.error("UserController: createUser execution. Either length is less than 7 or pass and conf pass do not match. Unable to create user.");
			return ResponseEntity.badRequest().build();
		}
		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
		userRepository.save(user);
		log.info("UserController: createUser execution FINISHED.");
		return ResponseEntity.ok(user);
	}
	
}
