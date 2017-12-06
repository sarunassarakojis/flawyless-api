package com.flawyless.service;

import com.flawyless.model.User;
import com.flawyless.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(userRepository.findOne(id));
    }

    public User saveUser(User userToSave) {
        return userRepository.save(userToSave);
    }

    public void deleteUser(long id) {
        userRepository.delete(id);
    }
}
