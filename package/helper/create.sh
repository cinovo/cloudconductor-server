#!/bin/bash
RPMSOURCE=""
CLOUDCONDUCTOR=localhost:8090
YUMPATH="/opt/cloudconductor/static/yum"

#copy versions from a source
if [ ! -z "$RPMSOURCE" ]; then
        if [ ! -z "$YUMPATH" ]; then
                echo "Starting to copy yum repos..."
                cp -n $RPMSOURCE/*.rpm $YUMPATH
        fi
fi

#selected yum repos
if [ ! -z "$YUMPATH" ]; then
        echo "Creating the yum repos"
        repomanage -o -k 3 -c $YUMPATH | xargs rm
        createrepo $YUMPATH
        if [ $? -gt 0 ]; then
                echo "Error creating repos"
                exit 1;
        fi

        echo "Updating the rpm index"
        # create CloudConductor file
        REQUIREQUERY="\{\"@class\":\"de.cinovo.cloudconductor.api.model.Dependency\",\"name\":\"%{REQUIRENAME}\", \"version\":\"%{REQUIREVERSION}\", \"operator\":\"%{REQUIREFLAGS:depflags}\", \"type\":\"REQUIRES\"\}, "
        PROVIDEQUERY="\{\"@class\":\"de.cinovo.cloudconductor.api.model.Dependency\",\"name\":\"%{PROVIDENAME}\", \"version\":\"%{PROVIDEVERSION}\", \"operator\":\"%{PROVIDEFLAGS:depflags}\", \"type\":\"PROVIDES\"\}, "
        CONFLICTQUERY="\{\"@class\":\"de.cinovo.cloudconductor.api.model.Dependency\",\"name\":\"%{CONFLICTNAME}\", \"version\":\"%{CONFLICTVERSION}\", \"operator\":\"%{CONFLICTFLAGS:depflags}\", \"type\":\"CONFLICTS\"\}, "
        DEPQUERY="\"dependencies\":\[$REQUIREQUERY, $PROVIDEQUERY, $CONFLICTQUERY\]"
        MAINQUERY="\{\"@class\":\"de.cinovo.cloudconductor.api.model.PackageVersion\", \"name\":\"%{NAME}\", \"version\":\"%{VERSION}-%{RELEASE}\", $DEPQUERY\}, "

        INDEX=$(find $YUMPATH -name "*.rpm" | xargs -Ixx rpm -qp --queryformat "$MAINQUERY" xx 2>/dev/null | sed -e s/"}, ]"/"}]"/g -e s/^/[/ -e s/,\ \$/]/ -e s/"}, , {"/"},{"/g)
        echo $INDEX > $YUMPATH/index.cfg
fi

#notify config server
echo "Notifying the config server @ $CLOUDCONDUCTOR"
INDEX=`cat $YUMPATH/index.cfg`
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
