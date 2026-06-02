package com.minimarket.mapper;

import com.minimarket.dto.UserResponse;
import com.minimarket.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getDocument(),
                user.getCreatedAt()
        );
    }
}