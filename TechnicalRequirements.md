# Introduction #

This document describes the prerequisites needed for developing and running OpenPEX. In terms of server resources, you require n number of nodes (where n > 1) to run as worker nodes in the OpenPEX system. These nodes must be 64bit capable and have the hardware VT extensions available to them. These nodes will have XenServer installed on them (described below) and will erase any previously installed operating systems on that machine. You also need a gateway node that runs the OpenPEX software. This node needs a Java environment and application server, and also a NFS mount to host the Virtual Machine Images. This node should be relatively capabl but does not have to be 64bit or have hardware VT extensions. We recommend Ubuntu 8.04 or 9.04 as the operating system on the gateway node.


# Details #

The following items are required to successfully develop and run OpenPEX.
  * NetBeans 6.5.1
  * IceFaces 1.8
  * XenServer 5
  * Glassfish v2 application server
  * Java 1.5 or higher
  * MySQL

# NetBeans #
NetBeans is the preferred development and deployment platform that is utilised for OpenPEX. Download the latest release from <a href='www.netbeans.org'>www.netbeans.org</a>. At the time of writing, this is version 6.5.1. Choose the "All" bundle to ensure you have all the necessary components.

# IceFaces #
We utilise IceFaces for the Web Portal inferface of OpenPEX. Download the latest NetBeans bundle for IceFaces from the IceFaces <a href='http://www.icefaces.org/main/downloads/os-downloads.iface'>website</a> and follow the enclosed instructions that describe how to install these modules inside NetBeans. At the time of writing, the latest IceFaces bundle was ICEfaces-1.8.0-NetBeans-6.5.1-modules-a.zip.

# XenServer #
Download the latest version of Citrix XenServer from <a href='http://www.citrix.com'>citrix.com</a>. <a href='http://www.citrix.com/lang/English/lp/lp_1688615.asp'>This link</a> should take you directly to the download page. Download the Installer and the Linux Guest Support iso's and create installation CD's from them. At the time of writing the latest version of the Installer was FREE\_XenServer-5.0.0-Update3-install.iso and the the Linux Guest Support was FREE\_XenServer-5.0.0-Update3-linux.iso. Consult the <a href='http://www.citrix.com/xenserver_documentation_free'>documentation</a> for assistance on installing XenServer on your machines.

# Gateway node #
Here we will explain how to configure the gateway node to run OpenPEX. These instructions have been tested for a physical installation of Ubuntu 8.0.4 LTS, however they should work for Ubuntu 9.0.4.

Install a copy of Ubuntu Server 8.0.4 (or 9.0.4) onto the node you wish to utilise as the gateway. Once the installation has completed and the node has rebooted, execute the following commands to install the required software:

```
%apt-get update
%apt-get upgrade
%apt-get install mysql-server
%apt-get install libmysql-java
%apt-get install sun-java5-jdk
%apt-get install glassfishv2
%apt-get install apache2 phpmyadmin
```

`%cp /usr/share/java/mysql-connector-java-5.1.5.jar /usr/share/glassfishv2/lib`


`%vi /usr/bin/asadmin`

Change

GF\_DOMAIN\_DIR=$HOME/glassfishv2

To,

GF\_DOMAIN\_DIR=/var/lib/glassfishv2/domains

## Add PEX MySQL database and user ##

Open /etc/mysql/my.cnf and comment out the `bind-address` line as follows:

# bind-address          = 127.0.0.1

`%mysql -u root -p`

`mysql> CREATE DATABASE pex;`

`mysql> GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, INDEX, ALTER, CREATE TEMPORARY TABLES, LOCK TABLES ON pex.* TO 'pexadmin'@'localhost' IDENTIFIED BY 'yourpass';`

## Change the default glassfish passwords ##

Go to http://yourip:4848/ and click on Domain->Administrator Password

Change the password from the default ('adminadmin') to the password of your choice. Click 'Save'

## Add MySQL Java support to glassfish ##
While still in the glassfish management console, click on Resources->JDBC->Connection Pools. Click on 'New'.

Under General Settings, set the name as 'msqlPool', the Resource Type as 'javax.sql.DataSource' and the Database Vendor as MySQL. Hit 'Next'.

Under Additional Properties set the User as root and the password to be the root MySQL password you set earlier. Click 'Finish' when done.

Under Resources> JDBC> Connection Pools the 'msqlPool' should be listed. Click on the JNDI link and then 'Ping' this connection to verify it works.

## Prepare the NFS mount points for Operating System install media and Virtual Machine Images ##

We need to set up the NFS mount points that will host the install media (for installing operating systems into virtual machines) and for the virtual machines themselves. Extensive documentation on setting up NFS mount points on Ubuntu is available [here](https://help.ubuntu.com/community/SettingUpNFSHowTo), but the brief steps are as follows. In the example below, tyrellcorp is my gateway node and roy, pris, leon, deckard and zhora are my XenServer worker nodes. I am setting up `/vimages/` to store the Virtual Machine images, and `\isostore` for the iso images of installation media. `128.250.xx.xxx/255.255.255.xxx` is the IP address range and subnet of my cluster:

```
brobergj@tyrellcorp:~$ sudo vi /etc/hosts.allow 
# /etc/hosts.allow: list of hosts that are allowed to access the system.
#                   See the manual pages hosts_access(5) and hosts_options(5).
#
# Example:    ALL: LOCAL @some_netgroup
#             ALL: .foobar.edu EXCEPT terminalserver.foobar.edu
#
# If you're going to protect the portmapper use the name "portmap" for the
# daemon name. Remember that you can only use the keyword "ALL" and IP
# addresses (NOT host or domain names) for the portmapper, as well as for
# rpc.mountd (the NFS mount daemon). See portmap(8) and rpc.mountd(8)
# for further information.
#
portmap mountd nfsd statd lockd rquotad : 128.250.xx.xxx/255.255.255.xxx

brobergj@tyrellcorp:~$ sudo apt-get install portmap nfs-kernel-server

brobergj@tyrellcorp:~$ sudo mkdir /vimages

brobergj@tyrellcorp:~$ sudo mkdir /isostore

brobergj@tyrellcorp:~$ cat /etc/exports 
# /etc/exports: the access control list for filesystems which may be exported
#		to NFS clients.  See exports(5).
#
# Example for NFSv2 and NFSv3:
# /srv/homes       hostname1(rw,async) hostname2(ro,async)
#
# Example for NFSv4:
# /srv/nfs4        gss/krb5i(rw,async,fsid=0,crossmnt)
# /srv/nfs4/homes  gss/krb5i(rw,async)
#
/vimages roy(async,rw,no_root_squash) pris(async,rw,no_root_squash) leon(async,rw,no_root_squash) deckard(async,rw,no_root_squash) zhora(async,rw,no_root_squash)

/isostore roy(async,rw,no_root_squash) pris(async,rw,no_root_squash) leon(async,rw,no_root_squash) deckard(async,rw,no_root_squash) zhora(async,rw,no_root_squash)

brobergj@tyrellcorp:~$ sudo exportfs -ra

brobergj@tyrellcorp:~$ sudo /etc/init.d/portmap restart

brobergj@tyrellcorp:~$ sudo /etc/init.d/nfs-kernel-server restart
```


# Preparing the XenServer Resource Pool #

Install the XenCenter software on an available Windows machine that can easily connect over the network to the XenServer nodes you have installed. The XenCenter software is available on the XenServer installation media or can be downloaded separately [here](http://www.citrix.com/lang/English/lp/lp_1688615.asp). You need to create a Resource Pool to group all the worker nodes together. Consult the [XenSever Documentation](http://docs.xensource.com/XenServer/5.0.0/1.0/en_gb/) for the steps on creating a resource pool. Give the pool a name (such as "PEX") and make a node of which node you have chosen to be the Pool Master. Add your NFS mount points as "shared storage" to the Resource Pool you just created.

Using XenCenter, create some virtual machine images (e.g. Debian, CentOS, Windows, etc) to use as PEX templates. Ensure that these Virtual Machine images have "PEX" in their name, and that they are stored on the "shared storage" in the PEX Resource Pool, so OpenPEX can pick them up. Once you are satisfied with the state of the virtual machines you have created, convert them to templates by right clicking on them one by one, and choosing the "Covert to template" option.