package com.reboot.playmoney.dto;

import com.reboot.playmoney.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddUserRequest {
    private String email;
    private String password;

    public AddUserRequest(Member member) {
        this.email = member.getEmail();
        this.password = member.getPassword();

    }
}
