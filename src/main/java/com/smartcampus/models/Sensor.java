package com.smartcampus.models;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author mario
 */

import java.util.ArrayList;
import java.util.List;

public class Sensor {
    private String id;
    private String roomId;
    private String type;
    private String status;
    private double currentValue;
    private List<SensorReading> history = new ArrayList<>();

    public Sensor() {}
    // Getters/Setters
    public String getId() { 
        return id; 
    }
    public void setId(String id) { 
        this.id = id; 
    }
    public String getRoomId() { 
        return roomId; 
    }
    public void setRoomId(String roomId) { 
        this.roomId = roomId; 
    }
    public String getType() { 
        return type; 
    }
    public void setType(String type) { 
        this.type = type; 
    }
    public String getStatus() { 
        return status; 
    }
    public void setStatus(String status) { 
        this.status = status; 
    }
    public double getCurrentValue() { 
        return currentValue; 
    }
    public void setCurrentValue(double v) { 
        this.currentValue = v; 
    }
    public List<SensorReading> getHistory() { 
        return history; 
    }
    public void setHistory(List<SensorReading> h) { 
        this.history = h; 
    }
}