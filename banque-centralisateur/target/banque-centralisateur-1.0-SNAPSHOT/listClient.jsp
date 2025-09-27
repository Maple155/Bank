<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.banque.entity.Client" %>

<html>
<head>
    <title>Liste des clients</title>
</head>
<body>
<h1>Liste des clients</h1>
<table border="1">
    <tr>
        <th>ID</th>
        <th>Nom</th>
        <th>Prénom</th>
        <th>Email</th>
    </tr>

<%
    // On récupère l'attribut mis par le servlet
    List<Client> clients = (List<Client>) request.getAttribute("clients");
    if (clients != null) {
        for (Client client : clients) {
%>
    <tr>
        <td><%= client.getId() %></td>
        <td><%= client.getNom() %></td>
        <td><%= client.getPrenom() %></td>
        <td><%= client.getEmail() %></td>
    </tr>
<%
        }
    } else {
%>
    <tr>
        <td colspan="4">Aucun client trouvé.</td>
    </tr>
<%
    }
%>
</table>
</body>
</html>
