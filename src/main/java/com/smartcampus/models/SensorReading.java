package com.smartcampus.models;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author mario
 */


public class SensorReading {
    private String id;
    private long timestamp;
    private double value;

    public SensorReading() {}
    // Getters/Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long t) { this.timestamp = t; }
    public double getValue() { return value; }
    public void setValue(double v) { this.value = v; }
}