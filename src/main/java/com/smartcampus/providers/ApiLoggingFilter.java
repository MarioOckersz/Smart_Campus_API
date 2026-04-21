/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.providers;

/**
 *
 * @author mario
 */

import jakarta.ws.rs.container.*;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ApiLoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext req) { System.out.println("REQ: " + req.getMethod()); }
    @Override
    public void filter(ContainerRequestContext req, ContainerResponseContext res) { System.out.println("RES: " + res.getStatus()); }
}