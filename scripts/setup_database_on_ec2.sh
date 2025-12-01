#!/bin/bash
# Script de configuración de PostgreSQL en EC2
set -e

echo "=========================================="
echo "  CONFIGURACIÓN DE POSTGRESQL - BARIAPLUS"
echo "=========================================="

DB_NAME="${1:-bariaplus_db}"
DB_USER="${2:-bariaplus_user}"
DB_PASSWORD="${3:-defaultpassword}"

echo "📊 Base de datos: $DB_NAME"
echo "👤 Usuario: $DB_USER"

# 1. Verificar si PostgreSQL ya está instalado
if ! command -v psql &> /dev/null; then
    echo "⬇️  PostgreSQL no encontrado. Instalando..."
    sudo apt update -y
    sudo apt install -y postgresql postgresql-contrib
    sudo systemctl start postgresql
    sudo systemctl enable postgresql
    echo "✅ PostgreSQL instalado"
else
    echo "✅ PostgreSQL ya está instalado"
fi

# 2. Crear usuario si no existe
echo "👤 Configurando usuario de base de datos..."
sudo -u postgres psql -tc "SELECT 1 FROM pg_roles WHERE rolname='$DB_USER'" | grep -q 1 || \
    sudo -u postgres psql -c "CREATE USER $DB_USER WITH PASSWORD '$DB_PASSWORD';"
echo "✅ Usuario '$DB_USER' configurado"

# 3. Crear base de datos si no existe
echo "📊 Configurando base de datos..."
sudo -u postgres psql -tc "SELECT 1 FROM pg_database WHERE datname='$DB_NAME'" | grep -q 1 || \
    sudo -u postgres psql -c "CREATE DATABASE $DB_NAME OWNER $DB_USER;"
echo "✅ Base de datos '$DB_NAME' configurada"

# 4. Otorgar privilegios
echo "🔐 Otorgando privilegios..."
sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE $DB_NAME TO $DB_USER;"
sudo -u postgres psql -d "$DB_NAME" -c "GRANT ALL ON SCHEMA public TO $DB_USER;"

# 5. Configurar autenticación MD5
echo "🔧 Configurando autenticación..."
PG_HBA="/etc/postgresql/*/main/pg_hba.conf"
sudo sed -i 's/local   all             all                                     peer/local   all             all                                     md5/g' $PG_HBA
sudo sed -i 's/host    all             all             127.0.0.1\/32            ident/host    all             all             127.0.0.1\/32            md5/g' $PG_HBA

# 6. Aplicar esquema si existe
if [ -f "/opt/apps/backend/db/schema.sql" ]; then
    echo "📝 Aplicando esquema de base de datos..."
    sudo -u postgres PGPASSWORD="$DB_PASSWORD" psql -U "$DB_USER" -d "$DB_NAME" -f "/opt/apps/backend/db/schema.sql"
    echo "✅ Esquema aplicado correctamente"
else
    echo "⚠️  Archivo schema.sql no encontrado, se omite"
fi

# 7. Reiniciar PostgreSQL
echo "🔄 Reiniciando PostgreSQL..."
sudo systemctl restart postgresql

echo "=========================================="
echo "✅ CONFIGURACIÓN COMPLETADA"
echo "=========================================="