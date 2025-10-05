<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.banque.courant.entity.CompteCourant" %>

<%
    CompteCourant compte = (CompteCourant) request.getAttribute("compte");
    String error = (String) request.getAttribute("error");
%>

<html>
<head>
    <title>Opération Compte Courant</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/operation.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/sidebar.css">
</head>
<body>

<%-- Sidebar --%>
<%@ include file="sidebar.jsp" %>

<div class="main-content">
    <h1>Débiter ou Créditer votre compte</h1>

    <% if (error != null) { %>
        <h3 style="color:#dc2626;"><%= error %></h3>
    <% } %>

    <form method="POST" action="${pageContext.request.contextPath}/operation" class="container">
        <% if (compte != null) { %>
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

    <br>

    <%-- <% if(compte != null) { %>
        <a class="button-link" href="${pageContext.request.contextPath}/operation?compte=<%= compte.getId() %>">Débiter / Créditer le compte</a>
        <a class="button-link" href="${pageContext.request.contextPath}/connexionDepot?compte=<%= compte.getId() %>">Créer / Utiliser un compte dépôt</a>
        <a class="button-link" href="${pageContext.request.contextPath}/pret?compte=<%= compte.getId() %>">Demander / Rembourser un prêt</a>
    <% } %> --%>
</div>

</body>
</html>
