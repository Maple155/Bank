<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.banque.entity.Client" %>
<%@ page import="com.banque.entity.TypesStatut" %>
<%@ page import="com.banque.courant.entity.CompteCourant" %>
<%@ page import="com.banque.courant.entity.OperationCourant" %>
<%@ page import="com.banque.pret.entity.Pret" %>
<%@ page import="com.banque.pret.entity.PretStatut" %>
<%@ page import="com.banque.pret.entity.Remboursement" %>
<%@ page import="com.banque.courant.entity.Transaction" %>

<%
    CompteCourant compte = (CompteCourant) request.getAttribute("compte");
    double solde = (double) request.getAttribute("solde");

    List<OperationCourant> operationsCourant = (List<OperationCourant>) request.getAttribute("operationsCourant");
    List<Pret> prets = (List<Pret>) request.getAttribute("prets");
    Pret pretImpaye = (Pret) request.getAttribute("pretImpaye");
    List<Remboursement> remboursements = (List<Remboursement>) request.getAttribute("remboursements");
    double resteAPaye = (double) request.getAttribute("resteAPaye");

    List<Transaction> transactionsSender = (List<Transaction>) request.getAttribute("sender");
    List<Transaction> transactionsReceiver = (List<Transaction>) request.getAttribute("receiver");
%>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Espace client - Gestion de compte bancaire">
    <title>Espace Client - Banque</title>
    
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">

    <!-- CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/client.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/sidebar.css">
</head>
<body>
<% if(compte != null) { %>
    <%@ include file="sidebar.jsp" %>
    <main class="main-content">
        <!-- Header avec solde -->
        <header class="welcome-header">
            <div class="welcome-text">
                <h1>Bienvenue <%= compte.getClient().getNom() %></h1>
                <p class="subtitle">Votre espace bancaire personnel</p>
            </div>
            <div class="balance-card" role="status">
                <p class="balance-label">Solde actuel</p>
                <p class="balance-amount"><%= String.format("%,.2f", solde) %> <span class="currency">MGA</span></p>
                <p class="balance-footer">Compte N° <%= compte.getNumero() %></p>
            </div>
        </header>

        <!-- Opérations -->
        <% if (operationsCourant != null && !operationsCourant.isEmpty()) { %>
        <section class="operations-section">
            <h2>📋 Opérations courantes</h2>
            <div class="cards-grid">
                <% for(OperationCourant op : operationsCourant) { %>
                <article class="operation-card <%= op.getMontant() >= 0 ? "credit" : "debit" %>">
                    <header class="card-header">
                        <span class="operation-icon"><%= op.getMontant() >= 0 ? "➕" : "➖" %></span>
                        <span class="operation-amount"><%= String.format("%,.2f", Math.abs(op.getMontant())) %> MGA</span>
                    </header>
                    <footer class="card-footer">
                        <time class="operation-date"><%= op.getDateOperation() %></time>
                        <span class="operation-type"><%= op.getMontant() >= 0 ? "Crédit" : "Débit" %></span>
                    </footer>
                </article>
                <% } %>
            </div>
        </section>
        <% } %>

        <!-- Transactions envoyées -->
        <% if (transactionsSender != null && !transactionsSender.isEmpty()) { %>
        <section class="transactions-sent-section">
            <h2>💸 Transactions envoyées</h2>
            <div class="transactions-list">
                <% for(Transaction t : transactionsSender) { %>
                <article class="transaction-card sent">
                    <div class="transaction-left">
                        <div class="transaction-avatar"><%= t.getReceiver().getClient().getNom().substring(0, 1).toUpperCase() %></div>
                        <div class="transaction-info">
                            <div class="transaction-name"><%= t.getReceiver().getClient().getNom() %></div>
                            <time class="transaction-date"><%= t.getDateTransaction() %></time>
                        </div>
                    </div>
                    <div class="transaction-amount negative">-<%= String.format("%,.2f", t.getMontant()) %> MGA</div>
                </article>
                <% } %>
            </div>
        </section>
        <% } %>

        <!-- Transactions reçues -->
        <% if (transactionsReceiver != null && !transactionsReceiver.isEmpty()) { %>
        <section class="transactions-received-section">
            <h2>💰 Transactions reçues</h2>
            <div class="transactions-list">
                <% for(Transaction t : transactionsReceiver) { %>
                <article class="transaction-card received">
                    <div class="transaction-left">
                        <div class="transaction-avatar"><%= t.getSender().getClient().getNom().substring(0, 1).toUpperCase() %></div>
                        <div class="transaction-info">
                            <div class="transaction-name"><%= t.getSender().getClient().getNom() %></div>
                            <time class="transaction-date"><%= t.getDateTransaction() %></time>
                        </div>
                    </div>
                    <div class="transaction-amount positive">+<%= String.format("%,.2f", t.getMontant()) %> MGA</div>
                </article>
                <% } %>
            </div>
        </section>
        <% } %>

        <!-- Prêts -->
        <% if (prets != null && !prets.isEmpty()) { %>
        <section class="loans-section">
            <h2>🏦 Prêts associés</h2>
            <div class="loans-grid">
                <% for(Pret p : prets) { 
                    double reste = (pretImpaye != null && p.getId() == pretImpaye.getId()) ? resteAPaye : 0;
                    boolean isPaid = reste == 0;
                %>
                <article class="loan-card <%= isPaid ? "paid" : "active" %>">
                    <header class="loan-header">
                        <span class="loan-badge <%= isPaid ? "badge-success" : "badge-warning" %>">
                            <%= isPaid ? "✓ Rembourse" : "⏳ En cours" %>
                        </span>
                        <p class="loan-amount-main"><%= String.format("%,.2f", p.getMontant()) %> MGA</p>
                    </header>
                    <div class="loan-details">
                        <p>Date accord : <%= p.getDate_accord() %></p>
                        <p>Taux d'intérêt : <%= String.format("%.2f", p.getTaux()) %>%</p>
                        <p>Reste à payer : <span class="<%= isPaid ? "paid-text" : "due-text" %>"><%= String.format("%,.2f", reste) %> MGA</span></p>
                    </div>
                </article>
                <% } %>
            </div>
        </section>
        <% } %>

    </main>
<% } else { %>
    <div class="error-container">
        <h2>⚠️ Erreur : Compte non trouvé</h2>
        <p>Veuillez vous reconnecter ou contacter le support.</p>
    </div>
<% } %>
</body>
</html>
