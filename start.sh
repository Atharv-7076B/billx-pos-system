#!/bin/bash
set -e

MYSQL_DATA=/home/runner/mysql-data
MYSQL_RUN=/home/runner/mysql-run
MYSQL_CNF=/home/runner/my.cnf

mkdir -p "$MYSQL_RUN"

# Write MySQL config
cat > "$MYSQL_CNF" << 'MYCNF'
[mysqld]
user=runner
datadir=/home/runner/mysql-data
socket=/home/runner/mysql-run/mysql.sock
pid-file=/home/runner/mysql-run/mysql.pid
port=3306
bind-address=0.0.0.0
mysqlx=0
MYCNF

# Initialize data dir if needed
if [ ! -d "$MYSQL_DATA/mysql" ]; then
  echo "Initializing MySQL data directory..."
  mysqld --initialize-insecure --user=runner --datadir="$MYSQL_DATA" 2>&1
fi

# Start MySQL if not already running
if ! mysql -u root -h 127.0.0.1 -P 3306 --connect-timeout=2 -e "SELECT 1;" > /dev/null 2>&1; then
  echo "Starting MySQL..."
  rm -f "$MYSQL_RUN/mysql.sock" "$MYSQL_RUN/mysql.sock.lock" "$MYSQL_RUN/mysql.pid"
  mysqld --defaults-file="$MYSQL_CNF" --skip-networking=0 &
  # Wait for MySQL to be ready
  for i in $(seq 1 30); do
    if mysql -u root -h 127.0.0.1 -P 3306 --connect-timeout=2 -e "SELECT 1;" > /dev/null 2>&1; then
      echo "MySQL is ready."
      break
    fi
    echo "Waiting for MySQL... ($i/30)"
    sleep 1
  done
fi

# Set up database and credentials
mysql -u root -h 127.0.0.1 -P 3306 --connect-timeout=5 -e \
  "CREATE DATABASE IF NOT EXISTS pos_system;
   ALTER USER 'root'@'localhost' IDENTIFIED BY 'root123';
   FLUSH PRIVILEGES;" 2>&1 || \
mysql -u root -p'root123' -h 127.0.0.1 -P 3306 --connect-timeout=5 -e \
  "CREATE DATABASE IF NOT EXISTS pos_system;" 2>&1 || true

echo "Starting Spring Boot application..."
cd /home/runner/workspace/Backend/BillX-Pos-System
./mvnw spring-boot:run
