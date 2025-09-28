<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.List" %>
<%@ page import="com.banque.entity.Client" %>
<%@ page import="com.banque.courant.entity.CompteCourant" %>
<%@ page import="com.banque.courant.entity.OperationCourant" %>
<%@ page import="com.banque.pret.entity.Pret" %>
<%@ page import="com.banque.pret.entity.Remboursement" %>

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
</head>
<body>
<h1>Transaction</h1>

    <form action="${pageContext.request.contextPath}/transaction" method="POST">
        <input type="hidden" name="compte" value="<%= compte.getId() %>">
        <input type="hidden" name="action" value="search">

        <input type="text" name="numero" placeholder="Le numero du destinataire" required>
        <input type="submit" value="Rechercher">
    </form>
    <% if (error != null) { %>
        <h3><%= error %></h3>
    <% } %>
    <% if (message != null) { %>
        <h3><%= message %></h3>
    <% } %>
    <% if (compte_receiver != null) { %>
        <form action="${pageContext.request.contextPath}/transaction" method="POST">
        <input type="hidden" name="compte" value="<%= compte.getId() %>">
        <input type="hidden" name="action" value="send">
        <input type="hidden" name="receiver" value="<%= compte_receiver.getId() %>">

        <input type="text" name="numero" value="<%= compte_receiver.getNumero() %>" disable>
        <input type="number" min="0" name="montant" placeholder="10 000 000 MGA" required>
        <input type="submit" value="Envoyer">
    </form>
    <% } %>
</body>
</html>
