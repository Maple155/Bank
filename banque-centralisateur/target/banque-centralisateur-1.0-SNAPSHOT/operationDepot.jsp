<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.banque.entity.Client" %>
<%@ page import="com.banque.courant.entity.CompteCourant" %>
<%@ page import="com.banque.centralisateur.model.CompteDepot" %>

<%
    CompteCourant compteCourant = (CompteCourant) request.getAttribute("compteCourant");
    CompteDepot compteDepot = (CompteDepot) request.getAttribute("compteDepot");
    double solde = Double.valueOf(request.getAttribute("solde").toString());
    String error = (String) request.getAttribute("error");
%>
<html>
<head>
    <title>operation Depot</title>
</head>
<body>
    <h1>Debiter ou Crediter votre compte depot</h1>
        <h3> Votre solde est actuellement de <%= solde %> Ar </h3>
    <form method="POST" action="${pageContext.request.contextPath}/operationDepot">
        <% if (compteDepot != null) {%>
            <input type="hidden" name="compteDepot" value="<%= compteDepot.getId() %>">
            <input type="hidden" name="compteCourant" value="<%= compteCourant.getId() %>">
        <% } %>
        <% if (error != null ) {%>
            <h2> <%= error %> </h2>
        <% } %>
        <input type="number" min="0" name="montant" placeholder="ex: 1000000 Ar" required>
        <br>
        <br>
        <label for="action">Type d'operation :</label>
        <select name="action" id="action" required>
            <option value="">-- Sélectionnez l'operation --</option>
            <option value="crediter"> Crediter</option>
            <option value="debiter"> Debiter</option>
        </select>
        <br>
        <br>
        <input type="submit" value="Confirmer">
    </form>
</body>
</html>