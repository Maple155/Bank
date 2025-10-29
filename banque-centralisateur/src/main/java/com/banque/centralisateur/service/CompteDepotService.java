package com.banque.centralisateur.service;

import com.banque.centralisateur.model.CompteDepot;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.ArrayList;

@ApplicationScoped
public class CompteDepotService {

    private static final String DOTNET_API = "http://localhost:5240/api/comptedepot";
    // private static final String DOTNET_API = "http://localhost:5555/api/comptedepot";
    private final Client client = ClientBuilder.newClient();
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public CompteDepot getCompteDepot(int id) {
        try {
            String json = client.target(DOTNET_API + "/" + id)
                    .request(MediaType.APPLICATION_JSON)
                    .get(String.class);
            mapper.registerModule(new JavaTimeModule());
            return mapper.readValue(json, CompteDepot.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<CompteDepot> getAllComptes() {
        try {
            String json = client.target(DOTNET_API)
                    .request(MediaType.APPLICATION_JSON)
                    .get(String.class);
            mapper.registerModule(new JavaTimeModule());
            return mapper.readValue(json, new TypeReference<List<CompteDepot>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<CompteDepot> getComptesByClient(int clientId) {
        try {
            String json = client.target(DOTNET_API + "/by-client/" + clientId)
                    .request(MediaType.APPLICATION_JSON)
                    .get(String.class);
            mapper.registerModule(new JavaTimeModule());
            return mapper.readValue(json, new TypeReference<List<CompteDepot>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public CompteDepot getComptesByNumero(String numero) {
        try {
            String json = client.target(DOTNET_API + "/by-numero/" + numero)
                    .request(MediaType.APPLICATION_JSON)
                    .get(String.class);
            mapper.registerModule(new JavaTimeModule());
            return mapper.readValue(json, CompteDepot.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public CompteDepot createCompteDepot(CompteDepot compte) {
        try {
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // <- ne serialise pas les nulls
            String jsonRequest = mapper.writeValueAsString(compte);
            System.out.println(">>> JSON envoyé à l'API .NET:");
            System.out.println(jsonRequest);

            String jsonResponse = client.target(DOTNET_API)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(jsonRequest), String.class);

            mapper.registerModule(new JavaTimeModule());
            return mapper.readValue(jsonResponse, CompteDepot.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
