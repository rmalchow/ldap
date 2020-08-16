FROM openjdk:slim
MAINTAINER Ruben Malchow <ruben.malchow@mcon-group.com>
RUN mkdir /app 
WORKDIR /app
RUN apt-get update -y && apt-get install -y netcat && apt-get clean all -y
COPY ldap-app/target/ldap-app.jar ldap-app.jar
COPY ldap-app/config/application* /app/
COPY scripts/app.sh ldap-app.sh

CMD [ "/bin/bash", "ldap-app.sh" ]