<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.banque.courant.entity.CompteCourant" %>
<%@ page import="com.banque.pret.entity.Pret" %>

<%
    CompteCourant compte = (CompteCourant) request.getAttribute("compte");
    String error = (String) request.getAttribute("error");
    String message = (String) request.getAttribute("message");
%>

<html>
<head>
    <title>Prêt - Banque</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/sidebar.css">

    <style>
        .form-toggle-buttons {
            display: flex;
            justify-content: center;
            gap: 15px;
            margin-bottom: 20px;
        }

        .form-toggle-buttons button {
            background-color: #3b82f6;
            border: none;
            color: white;
            padding: 10px 20px;
            font-size: 16px;
            border-radius: 8px;
            cursor: pointer;
            transition: background-color 0.2s ease;
        }

        .form-toggle-buttons button.active {
            background-color: #2563eb;
        }

        .form-toggle-buttons button:hover {
            background-color: #2563eb;
        }

        .form-section {
            display: none;
            animation: fadeIn 0.3s ease-in-out;
        }

        .form-section.active {
            display: block;
        }

        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(10px); }
            to { opacity: 1; transform: translateY(0); }
        }

        .container {
            max-width: 500px;
            margin: 0 auto;
            background-color: #f8fafc;
            padding: 25px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }

        .container input, .container select {
            width: 100%;
            margin-bottom: 15px;
            padding: 10px;
            border-radius: 6px;
            border: 1px solid #ccc;
        }

        .container input[type="submit"] {
            background-color: #16a34a;
            color: white;
            cursor: pointer;
            border: none;
        }

        .container input[type="submit"]:hover {
            background-color: #15803d;
        }

        h1 {
            text-align: center;
            margin-bottom: 25px;
        }

        .error {
            color: #dc2626;
            text-align: center;
        }

        .success {
            color: #16a34a;
            text-align: center;
        }
    </style>
</head>
<body>

<%@ include file="sidebar.jsp" %>

<div class="main-content">
    <h1>Demande ou remboursement de prêt</h1>

    <% if (error != null) { %>
        <h3 class="error"><%= error %></h3>
    <% } %>
    <% if (message != null) { %>
        <h3 class="success"><%= message %></h3>
    <% } %>

    <!-- Boutons de bascule -->
    <div class="form-toggle-buttons">
        <button id="btnDemande" class="active" onclick="showForm('demande')">💰 Demander un prêt</button>
        <button id="btnRemboursement" onclick="showForm('remboursement')">💵 Rembourser un prêt</button>
    </div>

    <!-- Formulaire : DEMANDE DE PRÊT -->
    <form id="formDemande" class="form-section active container" method="POST" action="${pageContext.request.contextPath}/pret">
        <% if (compte != null) { %>
            <input type="hidden" name="compte" value="<%= compte.getId() %>">
        <% } %>
        <h2>Demande de prêt</h2>
        <input type="hidden" name="action" value="demander">

        <label>Montant demandé (MGA) :</label>
        <input type="number" min="0" name="montant" placeholder="Ex: 1 000 000" required>

        <label>Montant en lettres (MGA) :</label>
        <input type="text" name="montant_str" placeholder="Ex: Un Millions" required>

        <label>Date du pret :</label>
        <input type="date" name="date" required>

        <label>Durée (en mois) :</label>
        <input type="number" min="1" name="mois" placeholder="Ex: 12" required>

        <input type="submit" value="Demander le prêt">
    </form>

    <!-- Formulaire : REMBOURSEMENT -->
    <form id="formRemboursement" class="form-section container" method="POST" action="${pageContext.request.contextPath}/pret">
        <% if (compte != null) { %>
            <input type="hidden" name="compte" value="<%= compte.getId() %>">
        <% } %>
        <h2>Rembourser un prêt</h2>
        <input type="hidden" name="action" value="rembourser">

        <label>Reference du prêt :</label>
        <input type="number" min="0" name="pret" placeholder="ID du prêt à rembourser" required>

        <label>Montant à rembourser (MGA) :</label>
        <input type="number" min="0" name="montant" placeholder="Ex: 500 000" required>

        <input type="submit" value="Rembourser le prêt">
    </form>

    <!-- Script de bascule -->
    <script>
        function showForm(type) {
            const formDemande = document.getElementById('formDemande');
            const formRemboursement = document.getElementById('formRemboursement');
            const btnDemande = document.getElementById('btnDemande');
            const btnRemboursement = document.getElementById('btnRemboursement');

            if (type === 'demande') {
                formDemande.classList.add('active');
                formRemboursement.classList.remove('active');
                btnDemande.classList.add('active');
                btnRemboursement.classList.remove('active');
            } else {
                formDemande.classList.remove('active');
                formRemboursement.classList.add('active');
                btnDemande.classList.remove('active');
                btnRemboursement.classList.add('active');
            }
        }
    </script>

    <%
        Boolean downloadPDF = (Boolean) request.getAttribute("downloadPDF");
        String montant_str = (String) request.getAttribute("montant_str");
        if (downloadPDF != null && downloadPDF) {
    %>
    <script>
        window.onload = function() {
            window.location.href = '<%= request.getContextPath() %>/pret?download=1&compte=<%= compte.getId() %>&montant=<%= montant_str %>';
        };
    </script>
    <% } %>

</div>

</body>
</html>
