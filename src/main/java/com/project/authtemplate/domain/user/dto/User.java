package com.project.authtemplate.domain.user.dto;

import com.project.authtemplate.domain.user.domain.entity.UserEntity;
import com.project.authtemplate.domain.user.domain.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Builder
@Component
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String email;
    private String name;
    private String phoneNumber;
    private String password;
    private String imageUrl;
    private UserRole userRole;

    public User toUser(UserEntity userEntity){
        return User.builder()
                .email(userEntity.getEmail())
                .name(userEntity.getName())
                .password(userEntity.getPassword())
                .userRole(userEntity.getUserRole())
                .build();
    }

}
