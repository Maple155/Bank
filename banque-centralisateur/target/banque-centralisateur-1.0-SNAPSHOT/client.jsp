<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.banque.entity.Client" %>
<%@ page import="com.banque.courant.entity.CompteCourant" %>
<%@ page import="com.banque.courant.entity.OperationCourant" %>
<%@ page import="com.banque.pret.entity.Pret" %>
<%@ page import="com.banque.pret.entity.Remboursement" %>

<%
    CompteCourant compte = (CompteCourant) request.getAttribute("compte");
    double solde = (double) request.getAttribute("solde");

    List<OperationCourant> operationsCourant = (List<OperationCourant>) request.getAttribute("operationsCourant");
    List<Pret> prets = (List<Pret>) request.getAttribute("prets");
    Pret pretImpaye = (Pret) request.getAttribute("pretImpaye");
    List<Remboursement> remboursements = (List<Remboursement>) request.getAttribute("remboursements");
    double resteAPaye = (double) request.getAttribute("resteAPaye");
%>

<html>
<head>
    <title>Client</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
</head>
<body>
<% if(compte != null) { %>
    <%@ include file="sidebar.jsp" %>
    <div class="main-content">
        <h1>Bienvenue Mr / Mme <%= compte.getClient().getNom() %></h1>
        <h3>Votre solde est actuellement de <%= solde %> MGA</h3>

        <% if (operationsCourant != null) { %>
        <h2>Opérations sur le compte courant :</h2>
        <table>
            <tr>
                <th>Montant (MGA) </th>
                <th>Date</th>
            </tr>
            <% for(OperationCourant op : operationsCourant) { %>
                <tr>
                    <td><%= op.getMontant() %></td>
                    <td><%= op.getDateOperation() %></td>
                </tr>
            <% } %>
        </table>
        <% } %>
        <% if (prets != null) { %>
        <h2>Prêts associés :</h2>
        <table>
            <tr>
                <th>Montant (MGA) </th>
                <th>Date</th>
                <th>Taux</th>
                <th>Reste à payer</th>
            </tr>
            <% for(Pret p : prets) { %>
                <tr>
                    <td><%= p.getMontant() %></td>
                    <td><%= p.getDate_accord() %></td>
                    <td><%= p.getTaux() %></td>
                    <td><%= (p.getId() == pretImpaye.getId()) ? resteAPaye : 0 %></td>
                </tr>
            <% } %>
        </table>
        <% } %>
        <% if(pretImpaye != null && remboursements != null && !remboursements.isEmpty()) { %>
            <h3>Remboursements pour le prêt impayé :</h3>
            <table>
                <tr>
                    <th>Montant (MGA) </th>
                    <th>Date</th>
                </tr>
                <% for(Remboursement r : remboursements) { %>
                    <tr>
                        <td><%= r.getMontant() %></td>
                        <td><%= r.getDateRemboursement() %></td>
                    </tr>
                <% } %>
            </table>
        <% } %>
    </div>
<% } %>
</body>
</html>
