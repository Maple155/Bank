FROM quay.io/wildfly/wildfly:33.0.1.Final-jdk21

# Copie de ton JAR/WAR dans le dossier de déploiement de WildFly
COPY target/banque-change-1.0-SNAPSHOT.jar /opt/jboss/wildfly/standalone/deployments/
# Forcer le déploiement
RUN touch /opt/jboss/wildfly/standalone/deployments/banque-change-1.0-SNAPSHOT.jar.dodeploy

# Exposer le port HTTP
EXPOSE 8080

# Démarrer WildFly
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0"]


# docker stop banque-change
# docker rm banque-change
# docker run -d --name banque-change -p 8280:8080 -p 10091:9990 banque-change /opt/jboss/wildfly/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0


# Creer utilisateur test test (b) et test test (a) avec docker exec -it banque-change /bin/bash 
# puis /opt/jboss/wildfly/bin/add-user.sh 
# enfin verification cat /opt/jboss/wildfly/standalone/configuration/mgmt-users.properties