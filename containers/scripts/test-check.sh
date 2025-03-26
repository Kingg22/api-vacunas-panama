#!/bin/bash

# Get the password with secrets
export MSSQL_SA_PASSWORD=$(cat /run/secrets/MSSQL_SA_PASSWORD)

if /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "$MSSQL_SA_PASSWORD" -No -C -Q 'SELECT 1' -b -o /dev/null
then
  echo "success check"
  exit 0
else
  echo "failed query 1"
  exit 1
fi