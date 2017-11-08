#!/usr/bin/env bash

USERNAME=$1
PASSWORD=$2

sshpass -p "$PASSWORD" rsync -avz ../fe/dist/* "$USERNAME"@box15:/frontends/dashboardio/