package com.reboot.playmoney.dto;

import com.reboot.playmoney.domain.User;
import lombok.Getter;

@Getter
public class UserDto {
    private Long id;
    private String name;

    public UserDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
    }

    // getters
}