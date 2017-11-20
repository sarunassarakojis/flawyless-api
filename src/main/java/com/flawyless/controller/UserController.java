package com.flawyless.controller;

import com.flawyless.model.User;
import com.flawyless.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping(value = ControllerConstants.USER_API_URL)
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Collection<User> readAllUsers() {
        return userService.getAllUsers();
    }

    @RequestMapping(value = "/{id}")
    public ResponseEntity<User> readUser(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<User> addUser(@RequestBody User user) {
        User newUser = userService.saveUser(user);
        URI newUserLocation = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(newUser.getId()).toUri();

        return ResponseEntity.created(newUserLocation).body(newUser);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<User> updateUser(@RequestBody User newUser, @PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);

        newUser.setId(id);

        return user.isPresent() ? ResponseEntity.ok(userService.saveUser(newUser))
                : ResponseEntity.notFound().build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<User> deleteUser(@PathVariable Long id) {
        if (userService.getUserById(id).isPresent()) {
            userService.deleteUser(id);

            return ResponseEntity.ok().build();
        }

        return ResponseEntity.notFound().build();
    }
}
