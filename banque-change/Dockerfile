FROM quay.io/wildfly/wildfly:33.0.1.Final-jdk21

# Copie du WAR dans le dossier de déploiement
COPY target/banque-change.war /opt/jboss/wildfly/standalone/deployments/

# Forcer le déploiement
RUN touch /opt/jboss/wildfly/standalone/deployments/banque-change.war.dodeploy

# Démarrer WildFly
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0"]


# dans le terminal banque-change

# docker stop banque-change
# docker rm banque-change
# docker run -d --name banque-change -p 8280:8080 -p 10091:9990 banque-change /opt/jboss/wildfly/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0


# Creer utilisateur test test (b) et test test (a) avec docker exec -it banque-change /bin/bash 
# puis /opt/jboss/wildfly/bin/add-user.sh 
# enfin verification cat /opt/jboss/wildfly/standalone/configuration/mgmt-users.properties

# A chaque modif utiliser 
# docker cp target/banque-change.war banque-change:/opt/jboss/wildfly/standalone/deployments/