package com.example.jacekpodwysocki.soundie;


import static android.R.attr.path;

/**
 * Created by jacekpodwysocki on 04/11/2016.
 */

public class User {
    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    private String rank;


    public User(Integer Id,String firstName, String lastName, String Email, String Rank) {
        id=Id;
        firstname=firstName;
        lastname=lastName;
        email=Email;
        rank=Rank;
    }

    public Integer getId(){return id;}
    public String getFirstName(){return firstname;}
    public String getLastName(){return lastname;}
    public String getEmail(){return email;}
    public String getRank(){return rank;}

}
