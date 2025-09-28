<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.banque.courant.entity.CompteCourant" %>

<%
    CompteCourant compte = (CompteCourant) request.getAttribute("compte");
    double solde = (double) request.getAttribute("solde");

    CompteCourant compte_receiver = (CompteCourant) request.getAttribute("receiver");

    String error = (String) request.getAttribute("error");
    String message = (String) request.getAttribute("message");
%>

<html>
<head>
    <title>Transaction</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
</head>
<body>

<%-- Sidebar incluse --%>
<%@ include file="sidebar.jsp" %>

<div class="main-content">
    <h1>Transaction</h1>
    <h3>Votre solde : <%= solde %> Ar</h3>

    <% if (error != null) { %>
        <h3 style="color:#dc2626;"><%= error %></h3>
    <% } %>
    <% if (message != null) { %>
        <h3 style="color:#16a34a;"><%= message %></h3>
    <% } %>

    <%-- Formulaire recherche destinataire --%>
    <form action="${pageContext.request.contextPath}/transaction" method="POST" class="container">
        <input type="hidden" name="compte" value="<%= compte.getId() %>">
        <input type="hidden" name="action" value="search">

        <input type="text" name="numero" placeholder="Numéro du destinataire" required>
        <input type="submit" value="Rechercher">
    </form>

    <br><br>
    <%-- Formulaire envoi argent --%>
    <% if (compte_receiver != null) { %>
        <form action="${pageContext.request.contextPath}/transaction" method="POST" class="container">
            <input type="hidden" name="compte" value="<%= compte.getId() %>">
            <input type="hidden" name="action" value="send">
            <input type="hidden" name="receiver" value="<%= compte_receiver.getId() %>">

            <label>Compte destinataire :</label>
            <input type="text" value="<%= compte_receiver.getNumero() %>" disabled>

            <label>Montant :</label>
            <input type="number" min="0" name="montant" placeholder="Ex: 100000 Ar" required>

            <input type="submit" value="Envoyer">
        </form>
    <% } %>
</div>

</body>
</html>
