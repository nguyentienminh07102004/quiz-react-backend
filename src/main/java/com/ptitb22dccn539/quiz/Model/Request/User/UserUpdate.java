package com.ptitb22dccn539.quiz.Model.Request.User;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.List;

@Getter
@Setter
public class UserUpdate {
    private String id;
    private String email;
    private String firstname;
    private String lastname;
    private String address;
    private String avatar;
    private Date dateOfBirth;
    private List<String> roles;
    private String status;
}
