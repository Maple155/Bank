package com.banque.courant.ejb;

// import com.banque.entity.Direction;
// import com.banque.entity.Utilisateur;
// import com.banque.entity.Action_role;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.List;

@Startup
@Singleton
public class DatabaseInitializer {

    @PersistenceContext(unitName = "banquePU")
    private EntityManager em;

    @PostConstruct
    public void initialize() {
        try {
            System.out.println("=== DEBUT INITIALISATION BANQUE ===");

            // Vérifier avec un verrouillage base de données
            if (acquireLock()) {
                try {
                    // Vérifier si l'utilisateur admin existe déjà
                    // Long userCount = em.createQuery("SELECT COUNT(u) FROM Utilisateur u WHERE u.nom = 'admin'", Long.class)
                    //                   .getSingleResult();

                    // if (userCount == 0) {
                    //     System.out.println("Création des données initiales...");

                    //     // Création directions
                    //     Direction dirGen = new Direction("Direction Generale", 1);
                    //     Direction dirTech = new Direction("Direction Technique", 2);
                    //     em.persist(dirGen);
                    //     em.persist(dirTech);

                    //     em.flush(); // Force l'insertion pour avoir les IDs

                    //     // Création utilisateurs avec les bonnes directions
                    //     em.persist(new Utilisateur("admin", "admin123", dirGen, 1));
                    //     em.persist(new Utilisateur("tech", "tech123", dirTech, 2));

                    //     // Création des rôles UNIQUES
                    //     em.persist(new Action_role("operation_courant", 1, "CREATE"));
                    //     em.persist(new Action_role("operation_courant", 2, "VALIDATE"));
                    //     em.persist(new Action_role("operation_courant", 1, "VALIDATE"));

                    //     em.flush(); // Final flush
                    //     System.out.println("Donnees initialisees avec succès !");
                    // } else {
                    //     System.out.println("Donnees deja presentes (" + userCount + " utilisateurs admin trouves)");
                    // }
                } finally {
                    releaseLock();
                }
            } else {
                System.out.println("Initialisation déjà en cours par un autre module, skip...");
            }

            System.out.println("=== FIN INITIALISATION BANQUE ===");

        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean acquireLock() {
        try {
            // Solution 1: Vérifier d'abord, puis insérer
            Query checkQuery = em.createNativeQuery("SELECT locked FROM initialisation_lock WHERE id = 1");
            List<?> result = checkQuery.getResultList();
            
            if (!result.isEmpty()) {
                Boolean locked = (Boolean) result.get(0);
                if (locked) {
                    return false; // Déjà verrouillé
                }
            }
            
            // Essayer d'acquérir le verrou
            Query updateQuery = em.createNativeQuery("UPDATE initialisation_lock SET locked = true WHERE id = 1 AND locked = false");
            int updated = updateQuery.executeUpdate();
            
            return updated > 0;
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'acquisition du verrou: " + e.getMessage());
            return false;
        }
    }

    private void releaseLock() {
        try {
            em.createNativeQuery("UPDATE initialisation_lock SET locked = false WHERE id = 1")
              .executeUpdate();
        } catch (Exception e) {
            System.err.println("Erreur lors du relachement du verrou: " + e.getMessage());
        }
    }
}