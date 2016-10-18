package com.mecolab.memeticameandroid.Models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andres Matte on 8/10/2016.
 */
@Table(name="Users")
public class User extends Model {
    @Column(name="Name")
    public String mName;
    @Column(name="PhoneNumber")
    public String mPhoneNumber;

    public User() {
        super();
    }

    public User(String name, String phoneNumber) {
        mName = name;
        mPhoneNumber = phoneNumber;
    }

    public static User from_jsonObject (JSONObject jsonObject) throws JSONException {
        String name = jsonObject.getString("name");
        String phoneNumber = jsonObject.getString("phone_number");
        User user = new Select().from(User.class)
                .where("Name = ? AND PhoneNumber = ?", name, phoneNumber)
                .executeSingle();
        if (user==null) {
            user = new User(
                    name,
                    phoneNumber
            );
            user.save();
        }
        return user;
    }

    public static ArrayList<User> from_jsonArray(JSONArray jsonArray) throws JSONException {
        ArrayList<User> usersList = new ArrayList<User>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            User user = User.from_jsonObject(jsonObject);
            usersList.add(user);
        }

        return usersList;
    }

    public static ArrayList<User> getAll() {
        List<User> result = new Select()
                .from(User.class)
                .orderBy("Name ASC")
                .execute();
        return new ArrayList<User>(result);
    }
}
