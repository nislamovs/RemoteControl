#!/usr/bin/env bash

USERNAME="nizami@inbox.lv"
#USERNAME="sss@ddd.lv"
PASS="admin"

ENCRYPTED=`echo -ne "$USERNAME:$PASS" | base64`

curl -v 'http://localhost:8080/DashboardIO/auth/changepass/' \
 -H "Authorization: Basic $ENCRYPTED" \
 -H 'Host: localhost:8080' \
 -H "Content-Type: application/json" \
 -X PUT -d '{"name":"Nizami","lastName":"Islamovs","email":"nizami@inbox.lv","password":"admin1"}'
