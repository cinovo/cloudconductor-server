#!/bin/bash

# fill $C2CONFIG with cloudconductor.properties and export as variable
#
# read -r -d '' C2CONFIG << EOF
# key=value
# key=value
# EOF
# export C2CONFIG
# curl https://raw.githubusercontent.com/cinovo/cloudconductor-server/v2.13/cloud-init/full.sh | bash

rpm -ivh http://yum.cloudconductor.net/cloudconductor-2.14-1.noarch.rpm

echo "$C2CONFIG" > /opt/cloudconductor/cloudconductor.properties

service cloudconductor start