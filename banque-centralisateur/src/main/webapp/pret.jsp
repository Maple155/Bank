<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.banque.courant.entity.CompteCourant" %>

<%
    CompteCourant compte = (CompteCourant) request.getAttribute("compte");
    String error = (String) request.getAttribute("error");
    String message = (String) request.getAttribute("message");
%>

<html>
<head>
    <title>Prêt</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/sidebar.css">
</head>
<body>

<%-- Sidebar --%>
<%@ include file="sidebar.jsp" %>

<div class="main-content">
    <h1>Demander ou Rembourser un prêt</h1>

    <% if (error != null) { %>
        <h3 class="error"><%= error %></h3>
    <% } %>
    <% if (message != null) { %>
        <h3 class="success"><%= message %></h3>
    <% } %>

    <form method="POST" action="${pageContext.request.contextPath}/pret" class="container">
        <% if (compte != null) { %>
            <input type="hidden" name="compte" value="<%= compte.getId() %>">
        <% } %>

        <input type="number" min="0" name="montant" placeholder="Ex: 1000000 MGA" required>

        <label for="action">Type d'opération :</label>
        <select name="action" id="action" required>
            <option value="">-- Sélectionnez l'opération --</option>
            <option value="demander">Demander</option>
            <option value="rembourser">Rembourser</option>
        </select>

        <input type="submit" value="Confirmer">
    </form>

    <%-- <% if(compte != null) { %>
        <a class="button-link" href="${pageContext.request.contextPath}/operation?compte=<%= compte.getId() %>">Débiter / Créditer le compte</a>
        <a class="button-link" href="${pageContext.request.contextPath}/connexionDepot?compte=<%= compte.getId() %>">Créer / Utiliser un compte dépôt</a>
        <a class="button-link" href="${pageContext.request.contextPath}/pret?compte=<%= compte.getId() %>">Demander / Rembourser un prêt</a>
    <% } %> --%>
</div>

</body>
</html>
