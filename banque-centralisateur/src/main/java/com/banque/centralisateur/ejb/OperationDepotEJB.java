package com.banque.centralisateur.ejb;

import com.banque.centralisateur.model.OperationDepot;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.ArrayList;

@ApplicationScoped
public class OperationDepotEJB {

    private static final String DOTNET_API = "http://localhost:5240/api/operationdepot";
    private final Client client = ClientBuilder.newClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public OperationDepotEJB() {
        mapper.registerModule(new JavaTimeModule());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public OperationDepot getOperation(int id) {
        try {
            String json = client.target(DOTNET_API + "/" + id)
                    .request(MediaType.APPLICATION_JSON)
                    .get(String.class);
            return mapper.readValue(json, OperationDepot.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<OperationDepot> getAllOperations() {
        try {
            String json = client.target(DOTNET_API)
                    .request(MediaType.APPLICATION_JSON)
                    .get(String.class);
            return mapper.readValue(json, new TypeReference<List<OperationDepot>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<OperationDepot> getOperationsByCompte(int compteId) {
        try {
            String json = client.target(DOTNET_API + "/by-compte/" + compteId)
                    .request(MediaType.APPLICATION_JSON)
                    .get(String.class);
            return mapper.readValue(json, new TypeReference<List<OperationDepot>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public double getSoldeByCompte(int compteId) {
        try {
            String json = client.target(DOTNET_API + "/solde/" + compteId)
                    .request(MediaType.APPLICATION_JSON)
                    .get(String.class);
            return Double.parseDouble(json);
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    public OperationDepot createOperation(OperationDepot operation) {
        try {
            String jsonRequest = mapper.writeValueAsString(operation);
            System.out.println(">>> JSON envoyé à l'API .NET:");
            System.out.println(jsonRequest);

            String jsonResponse = client.target(DOTNET_API)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(jsonRequest), String.class);

            return mapper.readValue(jsonResponse, OperationDepot.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
