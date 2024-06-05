package com.example.eurcertificatecalc.auth.data.service;

import com.example.eurcertificatecalc.auth.data.entity.User;
import com.example.eurcertificatecalc.auth.data.repository.UserRepository;
import com.example.eurcertificatecalc.auth.model.UserRequest;
import com.example.eurcertificatecalc.auth.model.UserResponse;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDataService {

    private final UserRepository userRepository;

    public UserResponse create(UserRequest request) {

        throwIfExistsByName(request.getUsername());

        User user = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .build();

        return UserResponse.instance(userRepository.save(user));
    }

    public UserResponse update(UserRequest request , Long userId){

        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with id " + userId);
        }
        userRepository.updateUser(request.getUsername(), request.getPassword(), userId);

        return UserResponse.instance(findByUidInternal(userId));
    }

    public void delete(Long userUid) {
        userRepository.deleteById(userUid);
    }

    public UserResponse findById(Long userId) {

        return UserResponse.instance(findByUidInternal(userId));
    }

    public User findByName(String name) {
        return userRepository.findByUsername(name)
                .orElseThrow(() -> new EntityNotFoundException("Not found user by name:" + name));
    }

    public List<UserResponse> findAll() {
        return UserResponse.instance(userRepository.findAll());
    }

    private User findByUidInternal(Long userUid) {
        return userRepository.findById(userUid)
                .orElseThrow(() -> new EntityNotFoundException("Not found user by uid:" + userUid));
    }

    public boolean checkExistsByName(String name) {
        return userRepository.existsByUsername(name);
    }

    private void throwIfExistsByName(String name) {
        if(checkExistsByName(name))
            throw new EntityExistsException("User with name:["+ name +"] already exists");
    }

}