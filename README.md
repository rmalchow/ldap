## What this IS

this application is a small and simple UI to manage a basic LDAP setup. it currently works with an embedded Apache DS, and it can do basic user and group management tasks. it also allows you to delegate certain things (e.g. manage users of one group) without dealing with LDAP ACLs.

behind the scenes, all LDAP operations are executed by a single user, which will need extensive privileges. in the example setup, this is `cn=Directory Manager`, but you can create your own user if you want.

this application also ignores a lot of LDAP details, painting objects with rather coarse strokes. this may or may not be to your liking. for my personal usage, this is actually perfectly fine.

## What this is NOT

this is not a full-on replacement for actual LDAP management tools (e.g. Apache Directory Studio). while basic things are nicer in here, we achieve this by simply ignoring most of the finer points of LDAP: we only do the bare essentials.

## Scenarios

LDAP authentication is supported in a vast number of applications, and even mapping LDAP groups to local permissions is pretty popular. this is what this is for: scenarios where LDAP is not the central organziational platform, but rather a convenient single source of truth for authentication in other applications. if you insist on using LDAP trickery to implement your workflows, this is not for you.

so, this is for you if

- you want to manage media server, nextcloud and home automation access for you and your family at home
- you want to run build tools, a wiki and a git repository for the people in your local maker space.
- you are already running an compatible LDAP server in your small company, but you want to be able to delegate group management to someone else

i am planning to (eventually) include a bit more advanced functionality for editing LDAP objects, but this is never going to be THAT big (if you have urges, try FreeIPA or Keycloak or Privacy Idea). 

### Some Screenshots



#### Login Screen

![login_screen](docs/images/login_screen.png)



#### View LDAP Tree Structure

![login_screen](docs/images/tree.png)



#### Edit Group Members

![login_screen](docs/images/group-users.png)

## Installation

this can be run in different ways.

- as a standalone springboot app (just build it and check the [application.yml](ldap-app/config/application.yml))
- as a standalone [docker image](https://hub.docker.com/repository/docker/rmalchow/ldap) 
- in orchestration together with the 389 and a mariadb

the easiest way to run is the third one, you can check out the examples folder for a basic setup.

## Environment Variables, Directories, Ports

### LDAP container

- container port **8080**
- no volumes needed

| Name            | Value                  | Description                                                  |
| --------------- | ---------------------- | ------------------------------------------------------------ |
| URL             |                        | the public protocol and hostname for this instance, e.g. "https://users.example.com" |
| MYSQL_USERNAME  | "ldap"                 | the user to use on the DB. must have sufficient permissions to execute the flyway migrations (ALTER,DROP,CREATE etc) |
| MYSQL_PASSWORD  | [NONE]                 | the password for the DB user                                 |
| MYSQL_HOST      | "mysql"                | the hostname for the DB                                      |
| MYSQL_PORT      | "3306"                 | ... the port?                                                |
| MYSQL_SCHEMA    | "ldap"                 | the DB schema                                                |
| LDAP_PROTO      | "ldap"                 | wether to use LDAP or LDAPS. i will NOT implement LDAPS without checking the certs |
| LDAP_HOST       | "dirsrv"               | the LDAP host                                                |
| LDAP_PORT       | "389"                  | the LDAP port. note that i am not doing START_TLS            |
| LDAP_TYPE       | "ds389"                | currently, only 389 is supported. this may change to inlude openLDAP and apacheDS |
| LDAP_BASE       | "dc=foo.org"           | the root DN                                                  |
| LDAP_USER       | "cn=Directory Manager" | the user to use on the LDAP server. this should have pretty wide permissions. however, using the DirMgmr is probably bad. i will changes this |
| LDAP_PASS       | [NONE]                 | the corresponding password                                   |
| **SMTP CONFIG** |                        |                                                              |
| MAIL_HOST       |                        | smtp server name                                             |
| MAIL_PORT       |                        | the port to use for outgoing SMTP. this can be STARTTLS. if you are planning to use straight-up TLS, you also have to set: "spring_profiles_active=smtps" |
| MAIL_USER       |                        | username for the SMTP server                                 |
| MAIL_PASSWORD   |                        | password for the SMTP server                                 |
| MAIL_SENDER     |                        | email adress to use for SMTP envelope and reply-to headers   |

### MariaDB container

- container port **3306**
- one volume needed on **/var/lib/mysql**

This is a stock container. See their docs for details.

## APIs

this application also has all functionality available as a REST API. for details on the APIs, see:

​	http://{your instance}/apitester/index.html

### Obtaining Authentication Tokens

authentication is done by sending a POST request to the

​	/api/authenticate 

API endpoint with a username, password and (optionally) a list of required groupIds. the return status:

- **200** if everything is OK
- **401** if authentication failed (e.g. incorrect username / password)
- **403** if the user is not authorized (i.e. not a member of the given groups)
- **500** if authentication failed due to an internal error

if everything is ok, the response body will contain a user object, and the server will send a cookie containing a JWT token for this user.

----

**Attention** - JWT authentication tokens issued by the server are very short-lived and must be renewed in regular intervals

----

### Renewing Authentication Tokens

a simple POST call to the authentication endpoint without parameters, but with a valid JWT token will return the authenticated user in the body, along with a freshly issued JWT cookie.

