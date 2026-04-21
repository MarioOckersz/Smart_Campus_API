/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resources;

/**
 *
 * @author mario
 */

import com.smartcampus.data.DataStore;
import com.smartcampus.models.Room;
import com.smartcampus.exceptions.RoomNotEmptyException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.net.URI;
import java.util.ArrayList;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {
    private final DataStore store = DataStore.getInstance();

    @GET
    public Response getAll() { return Response.ok(new ArrayList<>(store.getRooms().values())).build(); }

    @POST
    public Response create(Room r, @Context UriInfo ui) {
        store.getRooms().put(r.getId(), r);
        URI uri = ui.getAbsolutePathBuilder().path(r.getId()).build();
        return Response.created(uri).entity(r).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        Room r = store.getRooms().get(id);
        if (r == null) return Response.status(404).build();
        if (!r.getSensorIds().isEmpty()) throw new RoomNotEmptyException("Room has sensors.");
        store.getRooms().remove(id);
        return Response.noContent().build();
    }
}