/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDateTime;

/**
 *
 * @author pablo
 */
public class Session {
    private final LocalDateTime timeStamp;
    private int hits;
    private int faults;

    public Session(LocalDateTime timeStamp, int hits, int faults) {
        this.timeStamp = timeStamp;
        this.hits = hits;
        this.faults = faults;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public int getHits() {
        return hits;
    }
    public void setHits(int h){
        hits = h;
    }
    
    public int getFaults() {
        return faults;
    }
    public void setFaults(int f){
        faults = f;
    }
}
