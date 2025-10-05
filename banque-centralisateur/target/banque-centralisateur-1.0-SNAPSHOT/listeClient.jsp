<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.banque.entity.Client" %>

<html>
<head>
    <title>Liste des clients</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/list.css">
</head>
<body>
<div class="main-content">
    <h1>Liste des clients</h1>
    <div class="card-container">
    <%
        List<Client> clients = (List<Client>) request.getAttribute("clients");
        if (clients != null && !clients.isEmpty()) {
            for (Client client : clients) {
    %>
        <div class="card">
            <h3><%= client.getNom() + " " + client.getPrenom() %></h3>
            <p><strong>Email:</strong> <%= client.getEmail() %></p>
            <p><strong>Adresse:</strong> <%= client.getAdresse() %></p>
            <form method="POST" action="${pageContext.request.contextPath}/banque">
                <input type="hidden" name="client" value="<%= client.getId() %>">
                <input type="submit" value="Détails" style="width:100%; margin-top:10px;">
            </form>
        </div>
    <%
            }
        } else {
    %>
        <p>Aucun client trouvé.</p>
    <%
        }
    %>
    </div>
</div>
</body>
</html>
