#!/usr/bin/env bash

USERNAME="nizami@inbox.lv"
PASS="admin"

ENCRYPTED=`echo -ne "$USERNAME:$PASS" | base64`

curl -v 'http://localhost:8080/DashboardIO/api/user/' \
 -H "Authorization: Basic $ENCRYPTED"
#
# -H 'Accept: application/json, text/plain, */*' \
# -H 'Connection: keep-alive' --compressed
