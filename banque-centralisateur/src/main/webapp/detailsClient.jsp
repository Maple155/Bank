<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.banque.entity.Client" %>
<%@ page import="com.banque.entity.TypesStatut" %>
<%@ page import="com.banque.courant.entity.CompteCourant" %>
<%@ page import="com.banque.centralisateur.model.CompteDepot" %>
<%@ page import="com.banque.pret.entity.Pret" %>
<%@ page import="com.banque.pret.entity.PretStatut" %>

<%
    Client client = (Client) request.getAttribute("client");
    List<CompteCourant> compteCourants = (List<CompteCourant>) request.getAttribute("compteCourants");
    List<CompteDepot> compteDepots = (List<CompteDepot>) request.getAttribute("compteDepots");
    List<PretStatut> prets = (List<PretStatut>) request.getAttribute("prets");
    double soldeTotal = (double) request.getAttribute("soldeTotal");
%>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Détails du Client - Banque</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/details.css">
</head>
<body>

<h1>Détails du Client</h1>

<!-- Informations client -->
<div class="section client-info">
    <p><strong>Nom :</strong> <%= client.getNom() %></p>
    <p><strong>Prénom :</strong> <%= client.getPrenom() %></p>
    <p><strong>Email :</strong> <%= client.getEmail() %></p>
    <p><strong>Adresse :</strong> <%= client.getAdresse() %></p>
    <p><strong>Date de naissance :</strong> <%= client.getDate_naissance() %></p>
    <p><strong>Solde total (courant + prêts impayés + depot) :</strong> <%= String.format("%,.2f MGA", request.getAttribute("soldeTotal")) %></p>
</div>

<!-- Comptes Courants -->
<h2>Comptes Courants</h2>
<div class="card-container">
<% for (CompteCourant c : compteCourants) { %>
    <div class="card">
        <h3>Compte n° <%= c.getNumero() %></h3>
        <p>Date d'ouverture : <%= c.getDateOuverture() %></p>
        <p>Code secret : <%= c.getCode_secret() %></p>
    </div>
<% } %>
</div>

<!-- Comptes Dépôts -->
<h2>Comptes Dépôts</h2>
<div class="card-container">
<% for (CompteDepot d : compteDepots) { %>
    <div class="card">
        <h3>Compte n° <%= d.getNumero() %></h3>
        <p>Date d'ouverture : <%= d.getDateOuverture() %></p>
        <p>Code secret : <%= d.getCodeSecret() %></p>
    </div>
<% } %>
</div>
<h2>Tous les Prêts</h2>
<div class="card-container">
<% 
    List<PretStatut> allPrets = (List<PretStatut>) request.getAttribute("allPrets");
    if (allPrets != null && !allPrets.isEmpty()) { 
        for (PretStatut p : allPrets) { 
            boolean isPaid = "rembourse".equalsIgnoreCase(p.getStatut().getType());
%>
    <div class="card">
        <h3>Montant : <%= String.format("%,.2f", p.getPret().getMontant()) %> MGA</h3>
        <p>Taux : <%= String.format("%.2f", p.getPret().getTaux()) %> %</p>
        <p>Date accord : <%= p.getPret().getDate_accord() %></p>
        <p>Compte associé : <%= p.getPret().getCompteCourant().getNumero() %></p>
        <p class="<%= isPaid ? "status-paid" : "status-due" %>">
            Statut : <%= p.getStatut().getType() %>
        </p>
        <% if ("en attente".equalsIgnoreCase(p.getStatut().getType())) { %>
            <p> 
                <a class="button-link button-validate"
                href="${pageContext.request.contextPath}/banque?client=<%= client.getId() %>&action=valider&pret=<%= p.getPret().getId() %>"> Valider </a>
                <a class="button-link button-refuse"
                href="${pageContext.request.contextPath}/banque?client=<%= client.getId() %>&action=refuser&pret=<%= p.getPret().getId() %>"> Refuser </a>
            </p>
        <% } %>
    </div>
<%     }
    } else { %>
    <p>Aucun prêt impayé.</p>
<% } %>
</div>

<!-- Prêts Impayés -->
<h2>Prêts Impayés</h2>
<div class="card-container">
<% if (prets != null && !prets.isEmpty()) { 
       for (PretStatut p : prets) { 
           boolean isPaid = "rembourse".equalsIgnoreCase(p.getStatut().getType());
%>
    <div class="card">
        <h3>Montant : <%= String.format("%,.2f", p.getPret().getMontant()) %> MGA</h3>
        <p>Taux : <%= String.format("%.2f", p.getPret().getTaux()) %> %</p>
        <p>Date accord : <%= p.getPret().getDate_accord() %></p>
        <p>Compte associé : <%= p.getPret().getCompteCourant().getNumero() %></p>
        <p class="<%= isPaid ? "status-paid" : "status-due" %>">
            Statut : <%= p.getStatut().getType() %>
        </p>
    </div>
<%     }
   } else { %>
    <p>Aucun prêt impayé.</p>
<% } %>
</div>

</body>
</html>
