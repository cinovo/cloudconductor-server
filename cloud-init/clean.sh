#!/bin/bash

# Usage
#
# curl https://raw.githubusercontent.com/cinovo/cloudconductor-server/v2.13/cloud-init/clean.sh | bash

rpm -ivh http://yum.cloudconductor.net/cloudconductor-2.14-1.noarch.rpm

service cloudconductor start