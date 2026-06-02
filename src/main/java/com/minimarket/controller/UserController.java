package com.minimarket.controller;

import com.minimarket.dto.CreateUserRequest;
import com.minimarket.dto.UpdateUserRequest;
import com.minimarket.dto.UserResponse;
import com.minimarket.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@RequestBody @Valid CreateUserRequest request) {
        //Json sera convertido no DTO CreateUserRequest
        return userService.create(request);
    }

    @GetMapping
    public List<UserResponse> findAll(){
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable Long id){
        return userService.findById(id);
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable long id, @RequestBody @Valid UpdateUserRequest request){
        return userService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id){
        userService.deleteById(id);
    }

}
