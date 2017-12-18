#!/usr/bin/env bash

USERNAME="nizami@inbox.lv"
PASS="admin"

ENCRYPTED=`echo -ne "$USERNAME:$PASS" | base64`

curl -v "http://localhost:8080/DashboardIO/auth/deleteuser/?username=$USERNAME" \
 -X DELETE -H "Authorization: Basic $ENCRYPTED" \
 -H 'Host: localhost:8080' \
 -H "Content-Type: application/json" \

