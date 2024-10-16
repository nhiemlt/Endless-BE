package com.datn.endless.dtos;

import com.datn.endless.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private String userID;
    private String username;
    private String fullname;
    private String phone;
    private String email;
    private String avatar;
    private String language;

    // Constructor to initialize from User entity
    public UserDTO(User user) {
        this.userID = user.getUserID();
        this.username = user.getUsername();
        this.fullname = user.getFullname();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.avatar = user.getAvatar();
        this.language = user.getLanguage();
    }
}
