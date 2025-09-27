<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.banque.entity.Client" %>
<%@ page import="com.banque.courant.entity.CompteCourant" %>

<%
    CompteCourant compte = (CompteCourant) request.getAttribute("compte");
    String error = (String) request.getAttribute("error");
%>
<html>
<head>
    <title>operation Courant</title>
</head>
<body>
    <h1>Debiter ou Crediter votre compte</h1>
    <form method="POST" action="${pageContext.request.contextPath}/operation">
        <% if (compte != null) {%>
            <input type="hidden" name="compte" value="<%= compte.getId() %>">
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