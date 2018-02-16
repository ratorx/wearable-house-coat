#!/bin/bash

if [ ! -e ".git" ]; then
    echo "Error: Should be run from base git directory"
    exit 1
fi

rm -rf 'WearableHouseCoat/app/src/main/java/clquebec/com/environment'

crsid=""
if [ $# -ne 1 ]; then
    echo -n "crsid: "
    read crsid
else
    crsid=$1
fi

scp -r "$crsid@shell.srcf.net:clquebec/environment" WearableHouseCoat/app/src/main/java/clquebec/com/environment
# find WearableHouseCoat/app/src/main/java/clquebec/com/environment/* -type f -exec git update-index --assume-unchanged '{}' 2>/dev/null \;
