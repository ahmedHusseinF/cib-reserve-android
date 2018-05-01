package com.cibeg.cibreserve;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Xyntechs on 3/25/2018.
 */

@IgnoreExtraProperties
public class User {
    String name;
    String email;
    String address;
    String mobile;

    /*Add more features to save about the users*/

    public User()
    {}
    public User(String namei, String emaili, String addressi, String mobilei)
    {
        this.address = addressi;
        this.mobile = mobilei;
        this.name = namei;
        this.email = emaili;
    }
}
