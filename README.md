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

    sqlite3 database.db < resources/db/database.schema
    setup two environment variables, authHeaderName and authHeaderValue
    `gradle build` followed by `gradle run`
    
Setting Up the Keystore
------------------------

Unfortunately, Java complicates consuming SSL certs. After installing the Let's Encrypt certs, you
will need to import that into a keystore.

    sudo openssl pkcs12 -export -in /etc/letsencrypt/live/kurtzbot.space/fullchain.pem \ 
    -inkey /etc/letsencrypt/live/kurtzbot.space/privkey.pem -out letsencrypt.p12 -name homeserver \ 
    -CAfile /etc/letsencrypt/live/kurtzbot.space/chain.pem -caname root

When prompted, provide a password to protect the keystore with. When the application starts up, it will read the
keystore, extract the cert, and then start serving HTTPS clients.

Automating Cert Renewal
-----------------------

TODO: Let's Encrypt rotates certs every 90 days, and the cronjob checks daily. As the earliest renewal allowed is 60
days, this means every two months the cert is regenerated and updated. This __needs__ to be pulled back into the
keystore and then the application must re-read the new file. I don't believe SparkJava will automatically read the
keystore file upon a change, so the easiest is to restart the whole application.

Yes, I will likely not do this in time before the cert is passed renewal.

*Why do today, what can be done tomorrow.*

Compatible Devices
------------------

[AC Controller](https://github.com/grnt426/HomeAcDevice) can control a window AC unit.