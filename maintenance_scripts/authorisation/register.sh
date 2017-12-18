#!/usr/bin/env bash

curl -v 'http://localhost:8080/DashboardIO/auth/register/' \
 -H 'Host: localhost:8080' \
 -H "Content-Type: application/json" \
 -X POST -d '{"name":"Nizami","lastName":"Islamovs","email":"nizami@inbox.lv","password":"admin"}'