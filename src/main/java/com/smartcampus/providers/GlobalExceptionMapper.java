/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.providers;

/**
 *
 * @author mario
 */




import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
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
        // If the exception is a WebApplicationException (like 404, 405, 415),
        // let Jersey's own mapper handle it – don't mess with it.
        if (exception instanceof WebApplicationException) {
            return ((WebApplicationException) exception).getResponse();
        }

        // For all truly unexpected exceptions, log the full stack trace securely,
        // then return a generic 500 with a JSON body and the correct media type.
        LOG.log(Level.SEVERE, "Unhandled internal error", exception);

        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)          // <-- critical fix
                .entity(ResponseHelper.format(500, "An unexpected internal server error occurred."))
                .build();
    }
}