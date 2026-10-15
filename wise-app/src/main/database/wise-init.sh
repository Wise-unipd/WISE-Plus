#!/bin/bash
set -e

# handle user creation and privilege assignment (halt on error)
psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d "$POSTGRES_DB" <<-EOSQL
    -- 1. Create the user to connect to the database
    CREATE USER $APP_DB_USER WITH PASSWORD '$APP_DB_PASS';

    -- 2. Make the new user the owner of the database
    ALTER DATABASE $POSTGRES_DB OWNER TO $APP_DB_USER;

    -- 3. Grant any additional permissions required
    GRANT ALL PRIVILEGES ON SCHEMA public TO $APP_DB_USER;
EOSQL

# make the password available for next commands
export PGPASSWORD=$APP_DB_PASS
# run SQL scripts using the new "limited" user
psql -v ON_ERROR_STOP=1 -U "$APP_DB_USER" -d "$POSTGRES_DB" -f /tmp/sql/wise-schema.sql
psql -v ON_ERROR_STOP=1 -U "$APP_DB_USER" -d "$POSTGRES_DB" -f /tmp/sql/wise-insert.sql