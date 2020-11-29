FROM harbor.cloudpowered.services/mcon-group-public/springboot-openjdk11:latest-alpine
MAINTAINER Ruben Malchow <ruben.malchow@mcon-group.com>
COPY ldap-app/target/ldap-app.jar ldap-app.jar
COPY ldap-app/config/application* /app/
#COPY scripts/app.sh ldap-app.sh
#CMD [ "/bin/bash", "ldap-app.sh" ]