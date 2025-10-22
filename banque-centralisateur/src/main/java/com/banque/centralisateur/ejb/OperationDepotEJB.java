package com.banque.centralisateur.ejb;

import com.banque.centralisateur.model.CompteDepot;
import com.banque.centralisateur.model.OperationDepot;
import com.banque.courant.dao.CompteCourantDAO;
import com.banque.courant.dao.OperationDAO;
import com.banque.courant.entity.CompteCourant;
import com.banque.courant.entity.OperationCourant;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

@ApplicationScoped
public class OperationDepotEJB {

    @EJB
    private CompteCourantDAO compteCourantDAO;
    @EJB
    private OperationDAO operationDAO;

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

            // 🔥 Debug : afficher le JSON brut
            // System.out.println("JSON brut reçu : " + json);

            // 🔥 Debug : afficher le JSON formaté avec Jackson
            Object jsonObject = mapper.readValue(json, Object.class);
            String prettyJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
            System.out.println("JSON formaté reçu :\n" + prettyJson);
            
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

    public void crediterCompte (CompteDepot compteDepot, CompteCourant compteCourant, double montant, LocalDateTime currDate) {
        OperationDepot opd = new OperationDepot(compteDepot.getId(), montant, currDate, true);
        this.createOperation(opd);

        OperationCourant opc = new OperationCourant(compteCourant, (montant * -1), Date.valueOf(LocalDate.now()), true);
        operationDAO.save(opc);
    }

    public void debiterCompte (CompteDepot compteDepot, CompteCourant compteCourant, double montant, LocalDateTime currDate) {
        OperationDepot opd = new OperationDepot(compteDepot.getId(), (montant * -1), currDate, true);
        this.createOperation(opd);

        OperationCourant opc = new OperationCourant(compteCourant, montant, Date.valueOf(LocalDate.now()), true);
        operationDAO.save(opc);
    }

    public LocalDateTime getLastDebitDate(List<OperationDepot> operationDepots)
    {
        LocalDateTime result = operationDepots.get(0).getDateOperation();

        for (OperationDepot operationDepot : operationDepots) {
            if (operationDepot.getMontant() < 0 && operationDepot.isValidate()) {
                result = operationDepot.getDateOperation();
            }
        }

        return result;
    }

    public boolean checkDebitOperation (List<OperationDepot> operationDepots) 
    {
        for (OperationDepot operationDepot : operationDepots) {
            if (operationDepot.getMontant() < 0 && operationDepot.isValidate()) {
                return true;
            }
        }
        return false;
    }
}
