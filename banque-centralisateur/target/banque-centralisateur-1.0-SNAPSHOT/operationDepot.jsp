<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.banque.courant.entity.CompteCourant" %>
<%@ page import="com.banque.centralisateur.model.CompteDepot" %>
<%@ page import="com.banque.centralisateur.model.OperationDepot" %>

<%
    CompteCourant compte = (CompteCourant) request.getAttribute("compte");
    CompteDepot compteDepot = (CompteDepot) request.getAttribute("compteDepot");
    double solde = Double.valueOf(request.getAttribute("solde").toString());
    String error = (String) request.getAttribute("error");
    List<OperationDepot> operations = (List<OperationDepot>) request.getAttribute("operations");
    
    double soldeInteret = 0;
    double interet = 0;
    int nbAnnee = 0;

    if (request.getAttribute("soldeInteret") != null) {
        nbAnnee = Integer.parseInt(request.getAttribute("nbAnnee").toString());
        soldeInteret = Double.parseDouble(request.getAttribute("soldeInteret").toString());
        interet = Double.parseDouble(request.getAttribute("interet").toString());
    }
%>

<html>
<head>
    <title>Opérations Compte Dépôt</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/operation.css">
</head>
<body>

<%-- Sidebar --%>
<%@ include file="sidebar.jsp" %>

<div class="main-content">
    <h1>Débiter ou Créditer votre compte dépôt</h1>
    <h3>Votre solde est actuellement de <%= solde %> MGA sans interet </h3>
    <% if (request.getAttribute("soldeInteret") != null) { %>
        <h3>Votre solde est actuellement de <%= soldeInteret %> MGA avec interet de <%= interet %> % </h3>
    <% } %>
    <% if (error != null) { %>
        <h3 style="color:#dc2626;"><%= error %></h3>
    <% } %>

    <form method="POST" action="${pageContext.request.contextPath}/operationDepot" class="container">
        <% if (compteDepot != null) { %>
            <input type="hidden" name="compteDepot" value="<%= compteDepot.getId() %>">
            <input type="hidden" name="compte" value="<%= compte.getId() %>">
        <% } %>

        <input type="number" min="0" name="montant" placeholder="Ex: 1000000 MGA" required>

        <label for="action">Type d'opération :</label>
        <select name="action" id="action" required>
            <option value="">-- Sélectionnez l'opération --</option>
            <option value="crediter">Créditer</option>
            <option value="debiter">Débiter</option>
        </select>

        <input type="submit" value="Confirmer">
    </form>

    <% if (operations != null) { %>
        <h2>Historique des opérations sur le compte dépôt :</h2>
        <table>
            <tr>
                <%-- <th>ID</th> --%>
                <th>Montant (MGA) </th>
                <th>Date</th>
            </tr>
            <% for (OperationDepot op : operations) { %>
                <tr>
                    <%-- <td><%= op.getId() %></td> --%>
                    <td><%= op.getMontant() %></td>
                    <td><%= op.getDateOperation() %></td>
                </tr>
            <% } %>
        </table>
    <% } else { %>
        <p>Aucune opération effectuée pour le moment.</p>
    <% } %>

    <%-- <% if(compteCourant != null) { %>
        <a class="button-link" href="${pageContext.request.contextPath}/operation?compte=<%= compteCourant.getId() %>">Débiter / Créditer le compte</a>
        <a class="button-link" href="${pageContext.request.contextPath}/connexionDepot?compte=<%= compteCourant.getId() %>">Créer / Utiliser un compte dépôt</a>
        <a class="button-link" href="${pageContext.request.contextPath}/pret?compte=<%= compteCourant.getId() %>">Demander / Rembourser un prêt</a>
    <% } %> --%>
</div>

</body>
</html>
