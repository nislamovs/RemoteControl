#!/usr/bin/env bash

USERNAME="nizami@inbox.lv"
PASS="admin"

ENCRYPTED=`echo -ne "$USERNAME:$PASS" | base64`

#echo "$ENCRYPTED"

curl -v "http://localhost:8080/DashboardIO/auth/resetpass/" \
 -H 'Host: localhost:8080' \
 -H "Content-Type: application/json" \
 -X PUT -d '{"email":"nizami@inbox.lv"}'
