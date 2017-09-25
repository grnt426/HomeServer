HomeServer
==========

Used to interface with various devices around my home through a web portal and REST. The REST API is used with
Alexa/AWS Lambda for voice support. MQTT Handles device messaging between all the devices.

More info about this project to come.

Requirements
------------

* Gradle 4.1 - used for building/running the project
* SQLite - persistent database
* ddclient - to allow for DynamicDNS, as my home does not have a static IP

Building The Project
--------------------

Create the Sqlite3 database file and initialize it with the proper schema.

    sqlite3 database.db < resources/db/database.schema
    
Next, authentication is currently handled very simply for clients which don't handle cookies/sessions (AWS Lambda), 
which will require setting up two environment variables, `authHeaderName` and `authHeaderValue`. If the client doesn't
have an authed session, the server will look for `authHeaderName` in the header of all requests, which must match
`authHeaderValue`. Requests will be rejected otherwise.

Finally, to build the project for the first time, run `gradle build`. From then on, `gradle run` is sufficient to start
the server.
    
Setting Up the Keystore
------------------------

Unfortunately, Java complicates consuming SSL certs. After installing the Let's Encrypt certs, you
will need to import that into a keystore.

    sudo openssl pkcs12 -export -in /etc/letsencrypt/live/domain.name/fullchain.pem \ 
    -inkey /etc/letsencrypt/live/domain.name/privkey.pem -out letsencrypt.p12 -name homeserver \ 
    -CAfile /etc/letsencrypt/live/domain.name/chain.pem -caname root

When prompted, provide a password to protect the keystore with. When the application starts up, it will read the
keystore, extract the cert, and then start serving HTTPS clients.

The server understands two environment variables, `keystorePassword` and `keystoreLocation`. If`keystoreLocation`
is not set, then the server will start in HTTP only mode and will not attempt to accept HTTPS connections.

Creating Images Folder
-----------------------

Place images within `resources/images`.

Automating Cert Renewal
-----------------------

TODO: Let's Encrypt rotates certs every 90 days, and the cronjob checks daily. As the earliest renewal allowed is 60
days, this means every two months the cert is regenerated and updated. This __needs__ to be pulled back into the
keystore and then the application must re-read the new file. I don't believe SparkJava will automatically read the
keystore file upon a change, so the easiest is to restart the whole server if the cert does update.

Currently, the cron job is setup to create its own HTTPS server and try to listen on port 443 for the domain ownership
confirmation ping. This __will not__ work as my router is configured to route 443 externally to 8443 internally.
Instead, SparkJava should serve up the hidden confirmation file itself so autocert-bot can do its job. 

Yes, I will likely not do this in time before the cert is passed renewal.

*Why do today, what can be done tomorrow.*

Compatible Devices
------------------

[AC Controller](https://github.com/grnt426/HomeAcDevice) can control a window AC unit.