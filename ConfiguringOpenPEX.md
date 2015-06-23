# Introduction #

This page will tell you how to configure OpenPEX to connect to and control a XenServer 5 cluster.


# Details #

First, checkout the code from the SVN using Netbeans. The recommended Netbeans version is 6.5.1 (with IceFaces 1.8 or greater installed). There are four files you need to edit to allow OpenPEX to connect to the XenServer cluster and the MySQL database. These files are:

  * `pex.properties`
  * `Test.java`
  * `TestAPI.java`

For `pex.properties` edit the file so it matches your XenServer and MySQL installation:
```
xen.username = root
xen.password = XXXXXXX
xen.url = http://XXXX.csse.unimelb.edu.au

db.username=XXXXXX
db.password=XXXXXX
db.url=jdbc:mysql://XXXXX.csse.unimelb.edu.au:3306/pex
```

`Test.java` and `TestAPI.java` are simple test cases. If you wish to use them you need to ensure the root and password match that of your XenServer pool master:
```
String username = "root";
String password = "XXXXXX";
```