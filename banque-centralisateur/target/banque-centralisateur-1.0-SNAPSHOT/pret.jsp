<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.banque.entity.Client" %>
<%@ page import="com.banque.courant.entity.CompteCourant" %>

<%
    CompteCourant compte = (CompteCourant) request.getAttribute("compte");
    String error = (String) request.getAttribute("error");
    String message = (String) request.getAttribute("message");
%>
<html>
<head>
    <title>Pret</title>
</head>
<body>
    <h1>Demander ou Rembourser un pret</h1>
    <form method="POST" action="${pageContext.request.contextPath}/pret">
        <% if (compte != null) {%>
            <input type="hidden" name="compte" value="<%= compte.getId() %>">
        <% } %>
        <% if (error != null ) {%>
            <h2> <%= error %> </h2>
        <% } %>
        <% if (message != null ) {%>
            <h2> <%= message %> </h2>
        <% } %>
        <input type="number" min="0" name="montant" placeholder="ex: 1000000 Ar" required>
        <br>
        <br>
        <label for="action">Type d'operation :</label>
        <select name="action" id="action" required>
            <option value="">-- Sélectionnez l'operation --</option>
            <option value="demander"> Demander</option>
            <option value="rembourser"> Rembourser</option>
        </select>
        <br>
        <br>
        <input type="submit" value="Confirmer">
    </form>
</body>
</html>