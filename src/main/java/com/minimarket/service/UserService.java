package com.minimarket.service;

import com.minimarket.dto.CreateUserRequest;
import com.minimarket.dto.UpdateUserRequest;
import com.minimarket.dto.UserResponse;
import com.minimarket.entity.User;
import com.minimarket.enums.UserRole;
import com.minimarket.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.minimarket.exception.BusinessException;
import java.util.List;
import com.minimarket.exception.ResourceNotFoundException;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse create(CreateUserRequest request){

        if (userRepository.existsByEmail(request.getEmail())){
            throw new BusinessException("Email is already registered.");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setPhone(request.getPhone());
        user.setRole(UserRole.CUSTOMER);

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getPhone(),
                savedUser.getRole()
        );

    }
//Metodo para get
    public List<UserResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getRole()
                ))
                .toList();
    }

    public UserResponse findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole()
        );
    }
    //metodo para update, validando email repetido nao duplicado
    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email is already registered.");
        }

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setPhone(request.getPhone());

        User updatedUser = userRepository.save(user);

        return new UserResponse(
                updatedUser.getId(),
                updatedUser.getName(),
                updatedUser.getEmail(),
                updatedUser.getPhone(),
                updatedUser.getRole()
        );

    }
//busca user by id primeiro
public void deleteById(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id" + id));

        userRepository.delete(user);
}
}
