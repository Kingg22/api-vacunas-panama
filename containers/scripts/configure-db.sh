#!/bin/bash

echo "Start database configure script"

# Get the password with secrets
export MSSQL_SA_PASSWORD=$(cat /tmp/sa_password.txt)

# Start SQL Server
/opt/mssql/bin/sqlservr &

echo "WAITING FOR SQL SERVER FINISH START"
# Between 10 and 15s Sql Server start complete, adjust if necessary
sleep 15

until /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "$MSSQL_SA_PASSWORD" -No -C -Q 'SELECT 1' &> /dev/null
do
  echo "Waiting for sql server..."
  sleep 5
done

echo "FINISH waiting for sql"

# Run the setup script to create the DB and the schema in the DB
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "$MSSQL_SA_PASSWORD" -No -C -d master -i /usr/src/app/setup.sql || { echo "Error ejecutando setup.sql"; exit 1; }
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "$MSSQL_SA_PASSWORD" -No -C -d vacunas -i /usr/src/app/test.sql || { echo "Error ejecutando test.sql"; exit 1; }
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "$MSSQL_SA_PASSWORD" -No -C -d vacunas -i /usr/src/app/user.sql || { echo "Error ejecutando user.sql"; exit 1; }

echo "Database setup sql executed"

echo "Eliminando txt temporal"
rm -f /tmp/sa_password.txt

echo "Database configure script finish"
