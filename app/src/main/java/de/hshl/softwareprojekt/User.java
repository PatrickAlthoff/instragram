package de.hshl.softwareprojekt;

import java.io.Serializable;

//Hält das User Objekt
public class User implements Serializable {
    private long id;
    private String username;
    private String email;
    private String base64;

    public User(long id, String username, String email) {

        this.id = id;
        this.username = username;
        this.email = email;
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
}
