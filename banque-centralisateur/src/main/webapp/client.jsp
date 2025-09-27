<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.banque.entity.Client" %>
<%@ page import="com.banque.courant.entity.CompteCourant" %>


<%
    CompteCourant compte = (CompteCourant) request.getAttribute("compte");
    double solde = (double) request.getAttribute("solde");
%>
<html>
<head>
    <title>Client</title>
</head>
<body>
    <h1>Success</h1>
    <% if(compte != null) { %>
        <h1> Bienvenue Mr / Mme <%= compte.getClient().getNom() %> </h1>
        <br>
            <h3> Votre solde est actuellement de <%= solde %><h3>
    <% } %>
    <a href="${pageContext.request.contextPath}/operation?compte=<%= compte.getId() %>"> Debiter / Crediter le compte</a>
    <br>
    <a href="#"> Creer un compte depot</a>
    <br>
    <a href="#"> Compte depot</a>
    <br>
    <a href="${pageContext.request.contextPath}/pret?compte=<%= compte.getId() %>"> Demander / Rembourser un pret</a>
</body>
</html>