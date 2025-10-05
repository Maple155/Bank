<!-- Bouton menu mobile -->
<%-- <button class="sidebar-toggle" id="sidebarToggle" aria-label="Ouvrir le menu">
    <span></span><span></span><span></span>
</button>
<div class="sidebar-overlay" id="sidebarOverlay"></div> --%>

<!-- Sidebar -->
<nav class="sidebar" id="sidebar" role="navigation" aria-label="Menu principal">
    <h3>
        <a href="${pageContext.request.contextPath}/" class="sidebar-link">Menu</a>
    </h3>
    <br><br>
    <ul>
        <li>
            <form method="POST" action="${pageContext.request.contextPath}/connexion">
                <input type="hidden" value="login" name="action">
                <input type="hidden" value="<%= compte.getNumero() %>" name="numero">
                <button type="submit" class="sidebar-link">Accueil</button>
            </form>
        </li>
        <br>
        <li><a href="${pageContext.request.contextPath}/operation?compte=<%= compte.getId() %>" class="sidebar-link">Debiter / Crediter</a></li>
        <br>
        <li><a href="${pageContext.request.contextPath}/transaction?compte=<%= compte.getId() %>" class="sidebar-link">Envoyer de l'argent</a></li>
        <br>
        <li><a href="${pageContext.request.contextPath}/connexionDepot?compte=<%= compte.getId() %>" class="sidebar-link">Compte depot</a></li>
        <br>
        <li><a href="${pageContext.request.contextPath}/pret?compte=<%= compte.getId() %>" class="sidebar-link">Prets</a></li>
        <br>
    </ul>
</nav>

<script>
const sidebarToggle = document.getElementById('sidebarToggle');
const sidebar = document.getElementById('sidebar');
const sidebarOverlay = document.getElementById('sidebarOverlay');

function toggleSidebar() {
    sidebar.classList.toggle('active');
    sidebarOverlay.classList.toggle('active');
    sidebarToggle.classList.toggle('active');
}

sidebarToggle.addEventListener('click', toggleSidebar);
sidebarOverlay.addEventListener('click', toggleSidebar);
</script>
