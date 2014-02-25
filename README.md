[![Build Status](https://travis-ci.org/cinovo/cloudconductor-server.png?branch=master)](https://travis-ci.org/cinovo/cloudconductor-server)


# Why use CloudConductor
We needed some tooling which would allow easy and fast deployment of multiple services and configurations to a vast bulk of machines while keeping the installation and maintenance time as low as possible.

Since already existing solutions like *Puppet* or *Chef* didn't work in our environment and were a pure nightmare to install within it, we decided to build our own deployment system based on the following few points:

1. Use existing technology whereever possible. E.g. use the package manager provided by the OS
2. The host is the active part, providing flexibility in count and variants
3. Scale and run in a cloud environment with autoscaling and load balancing
4. Configuration and maintenance have to be as easy as possible
5. A running system first, error breakdown second

# Installation
### CloudConductor
1. Install or unpack the provided packages on your system. By default, the CloudConductor can be found at `/opt/cloudconductor/`
2. Modify the `cloudconductor.properties` to provide your database and login information:  
	`ds.type` : the database type. currently POSTGRESQL, MYSQL and HSQL are supported. (The use of HSQL is not recommended!)  
	`ds.host` : the hostname of the database, e.g. localhost.  
	`ds.port` : the port of the database, e.g. the default postgres port would be 5432.  
	`ds.username` : the username for database access  
	`ds.pw` : the password associated with the username  
	`ds.dbname` : the name of the database  to use  
	`svc.port` : the port used by this CloudConductor instance  
	`cloudconductor.username` : the login name for the CloudConductor web client  
	`cloudconductor.password` : the password for the CloudConductor web client  

3. Create the database, defined within the `cloudconductor.properties` file
3. Start CloudConductor
4. Add packages to the CloudConductor (see **How To add Packages**)
5. Go to the web frontend (default: `http://<YourIP>:8090/web`) and configure your environment

#####How To add Packages
Since CloudConductor utilises an already existing package manager, it's necessary to provide a list of existing packages. We provide a script for the yum package manager (RedHat), to be found at `/opt/cloudconductor/helper/create.sh` after installation.
Please Modify the `CONFIGSERVER` and `YUMPATH` variables, with `CONFIGSERVER` being the url and port of the CloudConductor to address, and the local yum repo as `YUMPATH`.


### CloudConductor-Agent
1. Install or unpack the provided packages on your system. By default, the CloudConductor-Agent can be found at `/opt/cloudconductor-agent/`
2. Provide the template name and the url of your CloudConductor within the env.sh file at `/home/core/env.sh`   
	`CONFIGSERVER_URL`: the url and port of the CloudConductor, e.g. `localhost:8090`  
    `TEMPLATE_NAME`: the name of the template the host should use, e.g. `TEMPLATE_A`
3. Start the CloudConductor-Agent

# How it works

####General 
The deployment system consists of two different parts:
1. The CloudConductor, providing the configuration and control mechanism.
2. The CloudConductor-Agent, providing the installation and handling routines on each host.

The CloudConductor-Agent contacts the CloudConductor in a 2 minute time interval, providing the following data:

Phase 1:
- Hostname
- Template
- Installed packages of the given repository

CloudConductor checks whether the installed packages match those defined within the corresponding template. The reply to the CloudConductor-Agent consists of install, remove and update commands for every package not matching the template.

Phase 2:
- Hostname
- Template
- Running services

CloudConductor matches the reported service states with those defined for the host. The reply consists of start, stop and restart commands, if any needed. In addition a list of all files associated with the template will be added, leaving the decision whether to overwrite an existing file to the CloudConductor-Agent. By default, the CloudConductor-Agent's decision is based on a md5-checksums comparison.

####Packages
All packages available within the package manager are listed in this area, sorted by package and version. Installing a specific package or downgrading already installed packages to older versions may be done via this overview.

For a how to on adding packages please see **How To add Packages**.

####Templates
A template provides the configuration of a host type. It specifies all installed packages and the default status of services associated with those packages. Additionally a list of hosts, using the template is provided.

The name of a template has to be unique.
The associated package source will be communicated to the CloudConductor-Agent.
If Auto Update is enabled, packages used by the template will be updated to the newest version if additional packages are added.

####Hosts
The "Hosts" page provides a list of all hosts, which communicated with the CloudConductor within the last three hours.

Each host displays a list of running services and their state. The state may be out of sync up to two minutes (CloudConductor-Agent provides the current states every two minutes by default). It's possible to change the state of a service, again taking up to two minutes until the changes takes effect.

The *Package differences to template* section differs between the last provided package versions and the versions defined within the template.
If any discrepancies were found, a list of packages will be displayed, providing additional information on the differences. Otherwise *In sync with template.* will be displayed.

If a hosts stops communicating with the CloudConductor for longer than three hours, it will be deleted from the "Hosts" list. This prevents flooding of the hosts list in an environment utilising autoscaling.

####Services
A service is the named init script of a package. It has to be associated with one or more packages. If the associated package is added to a template, the service will be available for every host using this template.

The default state of a newly added service within a template is "stopped".

####Config
CloudConductor provides the option to define key-value pairs, based on a template and/or services.
E.g. share some information on which database to use:
- `database.host`  = localhost
- `database.port` = 5432

These values may now be accessed by any program or script utilising the CloudConductor API.  
To provide some additional functionality, it's possible to define and overwrite the kv pairs in an hierarchical manner.

**Example:**
- GLOBAL: `logger.level` = ERROR
- GLOBAL SERVICE-A:`logger.level` = WARNING
- TEMPLATE-A: `logger.level` = INFO
- TEMPLATE-A SERVICE-A`logger.level` = DEBUG

This config would lead to: 
- SERVICE-A for TEMPLATE-A logging in DEBUG mode
- Everything in TEMPLATE-A logging INFOs
- Every SERVICE-A on hosts NOT using TEMPLATE-A logging WARNINGs
- Everything else logging only ERRORs

The CloudConductor API returns a list of kv pairs based on the dissolved hierarchy layers. 

####Files
CloudConductor supports the possibility to add additional scripts or configuration files for each template. It's possible to define the target path, fileowner, filegroup and filemode. Additionally the file content supports Velocity styled variables.
The CloudConductor-Agent  uses the md5 checksum of a file to guarantee the latest file version.

####SSHKeys
It's possible to manage multiple ssh keys for every template to provide support for access management to the host itself.

####Package Servers
CloudConductor supports the use of multiple package servers.

####Options
Provides some options to individualize CloudConductor's Web Frontend.

# FAQ

######**Will CloudConductor run in a cloud environment?**
Yes, it will. E.g. CloudConductor already is used within AWS. 
- [CloudConductor AMI](http://)
- [CloudConductor-Agent AMI](http://)

######**Which operating systems are supported?**
The CloudConductor itself is useable in any environment providing java.
For the hosts there are currently the following CloudConductor-Agents available:
 - [RedHat CloudConductor-Agent](http://)

Other Clients may be released in the future or can be build using the [CloudConductor-Agent API](http://).  
Since the REST API consists of some HTTP calls, it's very easy to create new Clients.

######**What is a template**
A template is the representation of the package and configuration state of any host associated with it.
One template provides the configuration for an arbitrary number of hosts.

######**What is a host**
A Host is a single machine or VM. It's always associated with one template, which provides it's configuration.

######**What is a service?**
A package providing an initscript may be added as a service.

######**Where can i find the web frontend?**
The CloudConductor web frontend may be found at  
> `http://<YourIp>:<YourPort>/web`  
e.g.:`http://localhost:8090/web`

######**Where can i find the api?**
The CloudConductor API may be found at  
> `http://<YourIp>:<YourPort>/api`  
e.g.:`http://localhost:8090/api`

######**Where can logs be found**
The CloudConductor log can be found by default at `/opt/cloudconductor/log`.  
The CloudConductor-Agent log can be found at `/opt/cloudconductor-agent/log`

######**Why is `host_name` not listed under "hosts" **
1. The CloudConductor-Agent is not running on the host
2. The last time the CloudConductor-Agent communicated with CloudConductor has been more than 3 hours ago.


