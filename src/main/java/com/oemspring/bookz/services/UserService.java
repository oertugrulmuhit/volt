package com.oemspring.bookz.services;

import com.oemspring.bookz.models.User;
import com.oemspring.bookz.repos.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);

    }


}
