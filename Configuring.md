# Introduction #

This page will tell you how to configure OpenPEX to connect to and control a XenServer 5 cluster.


# Details #

First, checkout the code from the SVN using Netbeans. The recommended Netbeans version is 6.5.1 (with IceFaces 1.8 or greater installed). There are four files you need to edit to allow OpenPEX to connect to the XenServer cluster and the MySQL database. These files are:

  * `pex.properties`
  * `pex.nodes`
  * `Test.java`
  * `TestAPI.java`

For `pex.properties` edit the file so it matches your XenServer and MySQL installation. For xen, you are telling OpenPEX which server is the Pool Master, by giving the root password and the address of that server. You also need to specify the MySQL username and password that was defined in TechnicalRequirements as well as the url of the database running on the gateway:
```
xen.username = root
xen.password = XXXXXXX
xen.url = http://XXXX.XXXX.XXXX.XXXX

db.username=XXXXXX
db.password=XXXXXX
db.url=jdbc:mysql://XXXX.XXXX.XXXX.XXXX:3306/pex
```

For `pex.nodes` edit the file so it matches your deployment of XenServer worker nodes. An example is given below, please edit it so it matches your own setup:
```
leon 128.250.XX.XXX
roy 128.250.XX.XXX
zhora 128.250.XX.XXX
pris 128.250.XX.XXX
deckard 128.250.XX.XXX
```

`Test.java` and `TestAPI.java` are simple test cases. If you wish to use them you need to ensure the root and password match that of your XenServer pool master:
```
String username = "root";
String password = "XXXXXX";
```

Ensure that the glassfishv2 application server is running on your gateway node. To start the glassfishv2 server, you can type in the following command:

```
brobergj@tyrellcorp:~$ sudo asadmin start-domain domain1
```

You then need to add this server in NetBeans. To do this perform the following steps:
  1. Inside the NetBeans IDE, click on the "Services" tab at the top-left of the NetBeans IDE window.
  1. Right click on "Servers" and select "Add Server...".
  1. Choose the server type as "Glassfish V2"
  1. Give it a name (e.g. "GlassFish V2 OpenPEX") and hit "Next"
  1. Select "Register Remote Domain" and hit "Next"
  1. Enter the fully qualified hostname for the gateway node running the application server (e.g. "tyrellcorp.csse.unimelb.edu.au") and hit "Next".
  1. Enter the admin username and password that was defined in TechnicalRequirements and hit "Finish".

Under the projects tab, right-click on the OpenPEX project and select "Properties". Click on the "Run" option and select the server (i.e. "GlassFish V2 OpenPEX") you just defined previously. Now select "OK".

Now, right click on the OpenPEX project in the NetBeans IDE and select "Run". If you have set up your environment correctly, the OpenPEX code should be deployed to the application server running on the gateway and your default web browser should open and display the OpenPEX web portal (e.g. http://gateway-hostname:8080/OpenPEX).