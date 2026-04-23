/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.providers;

/**
 *
 * @author mario
 */



import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {
    
    private static final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable exception) {
        // FIX: Logs the full stack trace securely to the server console via Logger
        LOG.log(Level.SEVERE, "Unexpected error occurred.", exception);
        
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ResponseHelper.format(500, "An unexpected internal server error occurred."))
                .build();
    }
}