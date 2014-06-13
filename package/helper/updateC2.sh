#!/bin/bash
REPO=myrepo

CLOUDCONDUCTOR="localhost:8090"
INDEXURL="http://rpm.url.de/$REPO/index.c2"
INDEXFILE="/tmp/index.c2.tmp"

## read index.c2
wget $INDEXURL -O $INDEXFILE
INDEX=`cat $INDEXFILE`
rm -f $INDEXFILE

echo "Notifying the config server @ $CLOUDCONDUCTOR"
if [ ! -z "$INDEX" ]; then
        CONTENTTYPE="Content-Type:application/json;charset=UTF-8"
        RESULT=$(curl -w "%{http_code}" -i -H $CONTENTTYPE -d "$INDEX" "http://$CLOUDCONDUCTOR/api/io/versions" -o /dev/null)
        if [ $RESULT == "200" ]; then
                echo "Updated CloudConductor"
                exit 0;
        else
                echo "Error on updating CloudConductor. Error code : $RESULT";
                exit 1;
        fi
fi
