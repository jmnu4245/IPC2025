/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDate;
import java.util.ArrayList;
import javafx.scene.image.Image;

/**
 *
 * @author pablo
 */

public class User {
    private String nickName; 
    private String email;
    private String password;
    private Image avatar;
    private LocalDate birthdate;
    private ArrayList<Session> sessions;

    public User(String nickName, String email, String password, Image avatar, LocalDate birthdate) {
        this.nickName = nickName;
        this.email = email;
        this.password = password;
        this.avatar = avatar;
        this.birthdate = birthdate;
        this.sessions = new ArrayList<>();
    }

    // Getter for nickName (no setter, no puede ser actualizado)
    public String getNickName() {
        return nickName;
    }

    // Getter and setter for email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getter and setter for password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getter and setter for avatar
    public Image getAvatar() {
        return avatar;
    }

    public void setAvatar(Image avatar) {
        this.avatar = avatar;
    }

    // Getter and setter for birthdate
    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    // Getter and setter for sessions
    public ArrayList<Session> getSessions() {
        return sessions;
    }

    public void setSessions(ArrayList<Session> sessions) {
        this.sessions = sessions;
    }

    public void addSession(Session session) {
        this.sessions.add(session);
    }
}

