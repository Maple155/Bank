package com.banque.change.web;

import com.banque.change.entity.Change;
import com.banque.change.remote.ChangeRemote;

import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/change")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChangeResource {

    // Injection correcte de l'EJB
    @EJB
    private ChangeRemote changeService;

    @GET
    @Path("/devises")
    public List<String> getDevises() {
        return changeService.getDevisesUniques();
    }

    @GET
    public List<Change> all() {
        return changeService.all();
    }

    @GET
    @Path("/{devise}")
    public List<Change> findByDevise(@PathParam("devise") String devise) {
        return changeService.findByDevise(devise);
    }
}
