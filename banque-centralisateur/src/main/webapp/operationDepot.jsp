<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.banque.courant.entity.CompteCourant" %>
<%@ page import="com.banque.centralisateur.model.CompteDepot" %>
<%@ page import="com.banque.centralisateur.model.OperationDepot" %>

<%
    CompteCourant compte = (CompteCourant) request.getAttribute("compte");
    CompteDepot compteDepot = (CompteDepot) request.getAttribute("compteDepot");

    double solde = request.getAttribute("solde") != null ? (Double) request.getAttribute("solde") : 0;
    double soldeInteret = request.getAttribute("soldeInteret") != null ? (Double) request.getAttribute("soldeInteret") : 0;
    double interet = request.getAttribute("interet") != null ? (Double) request.getAttribute("interet") : 0;
    String error = (String) request.getAttribute("error");

    List<OperationDepot> operations = request.getAttribute("operations") != null ? 
        (List<OperationDepot>) request.getAttribute("operations") : new ArrayList<>();
%>


<html>
<head>
    <title>Opérations Compte Dépôt</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/operation5.css">
</head>
<body>

<%@ include file="sidebar.jsp" %>

<div class="main-content">
    <h1>Débiter ou Créditer votre compte dépôt</h1>

    <!-- Soldes avec couleur bleue -->
    <h3 class="solde">Votre solde est actuellement de <%= solde %> MGA sans intérêt</h3>
    <% if (request.getAttribute("soldeInteret") != null) { %>
        <h3 class="solde">Votre solde est actuellement de <%= soldeInteret %> MGA avec intérêt de <%= interet %> MGA </h3>
    <% } %>

    <!-- Message d'erreur -->
    <% if (error != null) { %>
        <h3 class="error"><%= error %></h3>
    <% } %>

    <!-- Formulaire -->
    <form method="POST" action="${pageContext.request.contextPath}/operationDepot" class="container">
        <% if (compteDepot != null) { %>
            <input type="hidden" name="compteDepot" value="<%= compteDepot.getId() %>">
            <input type="hidden" name="compte" value="<%= compte.getId() %>">
        <% } %>

        <div class="form-group">
            <label for="montant">Montant :</label>
            <input type="number" id="montant" min="0" name="montant" placeholder="Ex: 1000000 MGA" required>
        </div>

        <div class="form-group">
            <label for="date">Date de l'opération :</label>
            <input type="date" id="date" name="date" required>
        </div>

        <div class="form-group">
            <label for="action">Type d'opération :</label>
            <select name="action" id="action" required>
                <option value="">-- Sélectionnez l'opération --</option>
                <option value="crediter">Créditer</option>
                <option value="debiter">Débiter</option>
            </select>
        </div>

        <input type="submit" value="Confirmer">
    </form>

    <% if (operations != null) { %>
        <h2>Historique des opérations sur le compte dépôt :</h2>
        <table>
            <tr>
                <th>Montant (MGA)</th>
                <th>Date</th>
            </tr>
            <% 
                for (OperationDepot op : operations) { 
                   if (op.isValidate()) { 
            %>
                <tr>
                    <td><%= op.getMontant() %></td>
                    <td><%= op.getDateOperation() %></td>
                </tr>
            <% } } %>
        </table>
    <% } else { %>
        <p>Aucune opération effectuée pour le moment.</p>
    <% } %>
</div>

</body>
</html>
