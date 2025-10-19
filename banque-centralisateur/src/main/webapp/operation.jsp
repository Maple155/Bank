<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.banque.courant.entity.CompteCourant" %>

<%
    CompteCourant compte = (CompteCourant) request.getAttribute("compte");
    String error = (String) request.getAttribute("error");
%>

<html>
<head>
    <title>Opération Compte Courant</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/operation5.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/sidebar.css">
</head>
<body>

<%@ include file="sidebar.jsp" %>

<div class="main-content">
    <h1>Débiter ou Créditer votre compte</h1>

    <% if (error != null) { %>
        <h3><%= error %></h3>
    <% } %>

    <form method="POST" action="${pageContext.request.contextPath}/operation" class="container">
        <% if (compte != null) { %>
            <input type="hidden" name="compte" value="<%= compte.getId() %>">
        <% } %>

        <div class="form-group">
            <label for="montant">Montant :</label>
            <input type="number" id="montant" min="0" name="montant" placeholder="Ex : 1000000 MGA" required>
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

        <div class="form-actions">
            <input type="submit" value="Confirmer">
        </div>
    </form>
</div>

</body>
</html>
