#!/bin/bash

# Backup script
# Backup database and uploads to a git repository
# It is automatically called by the server

# Options

DB_OPTIONS="--skip-opt --comments=0 --set-charset --lock-tables --disable-keys --create-options --add-drop-table"
DIR="../BdeEnsisaBackup"

test -d $DIR || (echo "$DIR does not exist!" && exit 1)

# Clear existing files

test -d "$DIR/database" && rm -rf "$DIR/database"
test -d "$DIR/uploads" && rm -rf "$DIR/uploads"

# Database

mkdir -p "$DIR/database"
tables=$(mysql -NBA -h $DB_HOST -u $DB_USER -p$DB_PASSWORD -D $DB_NAME -e 'show tables')
tables=$(echo $tables | sed 's/LoginAuthorizes//g' | sed 's/NotificationsTokens//g' | sed 's/RegistrationRequests//g' | sed 's/PasswordRequests//g')
for t in $tables; do
    mysqldump --host="$DB_HOST" --result-file="$DIR/database/$t.sql" --user="$DB_USER" --password="$DB_PASSWORD" $DB_OPTIONS $DB_NAME $t
done

# Uploads

cp -r uploads "$DIR/uploads"

# Git

cd $DIR
git add .
git commit -m "Backup $(date)"
git push
