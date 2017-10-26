#!/usr/bin/env bash

#Description: this script is necessary to set up passwords for email boxed for smtp usage
#
#Run it from Jenkins job
#
#Usage
#./setPasswd.sh -dev <dev_pass> -prod <prod_pass>

DEV_PROPS_PATH="../be/src/main/resources/application-development.properties"
PROD_PROPS_PATH="../be/src/main/resources/application-production.properties"

DEV_PASSWD_STR="dev_pass"
PROD_PASSWD_STR="prod_pass"

PASSWD_PREFIX="alert.email.password"

while test ${#} -gt 0
do
    case "$1" in
      -prod)
            shift;
            PROD_PASSWD_STR="$PASSWD_PREFIX=$1"
            ;;
      -dev)
            shift;
            DEV_PASSWD_STR="$PASSWD_PREFIX=$1"
            ;;
      *)
            echo $"Usage: $0 { -dev dev_pass -prod prod_pass}"
            exit 1
    esac
    shift;
done

echo "Dev pass string  : $DEV_PASSWD_STR";
echo "Prod pass string : $PROD_PASSWD_STR";

sed -i "s/$PASSWD_PREFIX.*/$DEV_PASSWD_STR/g" $DEV_PROPS_PATH
sed -i "s/$PASSWD_PREFIX.*/$DEV_PASSWD_STR/g" $PROD_PROPS_PATH
