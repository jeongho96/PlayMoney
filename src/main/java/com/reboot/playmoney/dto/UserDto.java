package com.reboot.playmoney.dto;

import com.reboot.playmoney.domain.Member;
import lombok.Getter;

@Getter
public class UserDto {
    private Long id;
    private String name;

    public UserDto(Member member) {
        this.id = member.getMemberNumber();
        this.name = member.getName();
    }

    // getters
}