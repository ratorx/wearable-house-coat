#!/bin/bash

if [ ! -e ".git" ]; then
    echo "Error: Should be run from base git directory"
    exit 1
fi

rm -rf 'WearableHouseCoat/app/src/main/java/com/clquebec/environment'

crsid=""
if [ $# -ne 1 ]; then
    echo -n "crsid: "
    read crsid
else
    crsid=$1
fi

scp -r "$crsid@shell.srcf.net:clquebec/environment" WearableHouseCoat/app/src/main/java/com/clquebec/environment
find WearableHouseCoat/app/src/main/java/com/clquebec/environment/* -type f -exec git update-index --assume-unchanged '{}' 2>/dev/null \;
