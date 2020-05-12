## What this IS

this application is a small and simple UI to manage a basic LDAP setup. it currently works with 389ds, and it can do basic user and group management tasks. it also allows you to delegate certain things (e.g. manage users of one group) without dealing with LDAP ACLs.

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

## Installation

this can be run in different ways.

- as a standalone springboot app (just build it and check the [application.yml](ldap-app/config/application.yml))
- as a standalone [docker image](https://hub.docker.com/repository/docker/rmalchow/ldap) 
- in orchestration together with the 389 and a mariadb

the easiest way to run is the third one, you can check out the examples folder for a basic setup.

---

**Attention (a)** - the 389 image's init script does turn on the memberOf plugin. this is needed for proper operation. however, it is not actually loaded until the next restart. if you use the script from the examples folder, make sure you restart the 389 container before doing anything else.

---

**Attention (b)** - with the launch script in examples, a user with ui "admin" and the password given in DIRSRV_ADMIN_PASS is created. you can use this for your first login. in all other scenarios, the *FIRST USER* that logs into the UI with a correct uid / password will become the first admin in the UI.

----

**Attention (c)** - please make sure you double-check the port mappings and adjust them to your needs.

---

## Environment Variables, Directories, Ports

### LDAP container

- container port **8080**
- no volumes needed

| Name           | Value                  | Description                                                  |
| -------------- | ---------------------- | ------------------------------------------------------------ |
| MYSQL_USERNAME | "ldap"                 | the user to use on the DB. must have sufficient permissions to execute the flyway migrations (ALTER,DROP,CREATE etc) |
| MYSQL_PASSWORD | [NONE]                 | the password for the DB user                                 |
| MYSQL_HOST     | "mysql"                | the hostname for the DB                                      |
| MYSQL_PORT     | "3306"                 | ... the port?                                                |
| MYSQL_SCHEMA   | "ldap"                 | the DB schema                                                |
| LDAP_PROTO     | "ldap"                 | wether to use LDAP or LDAPS. i will NOT implement LDAPS without checking the certs |
| LDAP_HOST      | "dirsrv"               | the LDAP host                                                |
| LDAP_PORT      | "389"                  | the LDAP port. note that i am not doing START_TLS            |
| LDAP_TYPE      | "ds389"                | currently, only 389 is supported. this may change to inlude openLDAP and apacheDS |
| LDAP_BASE      | "dc=foo.org"           | the root DN                                                  |
| LDAP_USER      | "cn=Directory Manager" | the user to use on the LDAP server. this should have pretty wide permissions. however, using the DirMgmr is probably bad. i will changes this |
| LDAP_PASS      | [NONE]                 | the corresponding password                                   |
| SMTP_*         |                        | SMTP config: host, port, user, password                      |

### 389DS Container

- container ports **3389** and **3636**
- one volume needed on **/data**

These values are only read on first boot. Once the data dir is populated, these are ignored. However, on first boot, they are **REQUIRED**

##### Connection to the 389 

If you need to do any maintenance or setup, or if you just want to check what's going on inside the LDAP server, you may have to connect to it directly. my tool of choice for this is **apache directory studio**, and the connection settings should be:

- ldap://{you_ldap_host}:{your_ldap_port}
- username: "cn=Directory Manager"
- password: *as defined on first boot* 

you can then perform administrative tasks on the LDAP server (such as extended aci configuration).

##### ACIs in the 389 container

the init script will set up a number of default acis on the root entry. these aci may or may not have to be modified by you:

- `(targetattr="*")(version 3.0; acl "Admin all"; allow (all) userdn="ldap:///$DN_OF_ADMIN";)` - after the admin is created, an "all access" aci rule is defined. this is so that the admin user can also be used ad the LDAP UI user
- `(targetattr="*")(version 3.0; acl "bind users can read/search"; allow (read,search) userdn="ldap:///uid=*-bind-user,*";)` - this rule allows any uid that ends in "-bind-user" more privileges. this is so that other applications can use this type of uid to search for users in the tree
- `(targetattr="*")(version 3.0; acl "Read self"; allow (read,search,compare) userdn="ldap:///self";)` - this rule is set up so that each user can find itself. 

Documentation on the format of these acis is pretty sparse ... but you might have to deal with this at some point if you want to make the most of it. not very much, though

----

**Attention** - occasionally, if the container is not shut down properly, you may have to delete the slapd pid in `$DATA_DIR/run/`

----

| Name              | Description                                        | Example          |
| ----------------- | -------------------------------------------------- | ---------------- |
| DIRSRV_DOMAIN     | name of backend and top level entry. without "dc=" | example.com      |
| DIRSRV_ORG        | the top level "org" entry, without "o="            | example corp ltd |
| DIRSRV_MGMT_PASS  | password for the Directory Manager account         |                  |
| DIRSRV_ADMIN_PASS | password for the LDAP UI admin user account        |                  |

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

