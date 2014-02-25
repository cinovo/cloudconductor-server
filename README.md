[![Build Status](https://travis-ci.org/cinovo/cloudconductor-server.png?branch=master)](https://travis-ci.org/cinovo/cloudconductor-server)


# Why use CloudConductor
We needed some tooling which would allow easy and fast deployment of multiple services and configurations to a vast bulk of machines while keeping the installation and maintenance time as low as possible.

Since already existing solutions like *Puppet* or *Chef* didn't work in our environment and were a pure nightmare to install within it, we decided to build our own deployment system based on the following few points:

1. Use existing technology whereever possible. E.g. use the package manager provided by the OS
2. The host is the active part, providing flexibility in count and variants
3. Scale and run in a cloud environment with autoscaling and load balancing
4. Configuration and maintenance have to be as easy as possible
5. A running system first, error breakdown second

[Wiki](https://github.com/cinovo/cloudconductor-server/wiki)
