package de.hshl.softwareprojekt;

import java.io.Serializable;
//Hält das User Objekt
public class User implements Serializable {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String username;
    private String email;

    public User(String username, String email){

        this.username = username;
        this.email = email;
    }
}
