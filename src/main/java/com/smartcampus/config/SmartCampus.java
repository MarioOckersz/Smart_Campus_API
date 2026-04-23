/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.config;

/**
 *
 * @author mario
 */


import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api/v1")
public class SmartCampus extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        // Resources
        classes.add(com.smartcampus.resources.DiscoveryResource.class);
        classes.add(com.smartcampus.resources.RoomResource.class);
        classes.add(com.smartcampus.resources.SensorResource.class);
        // Providers
        classes.add(com.smartcampus.providers.RoomNotEmptyExceptionMapper.class);
        classes.add(com.smartcampus.providers.LinkedResourceNotFoundMapper.class);
        classes.add(com.smartcampus.providers.SensorUnavailableExceptionMapper.class);
        classes.add(com.smartcampus.providers.GlobalExceptionMapper.class);
        classes.add(com.smartcampus.providers.ApiLoggingFilter.class);
        return classes;
    }
}