-- Database configuration table
CREATE TABLE IF NOT EXISTS database_configs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ds_name VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    driver VARCHAR(255) NOT NULL,
    db_type VARCHAR(50) NOT NULL,
    connection_url VARCHAR(500) NOT NULL,
    license VARCHAR(1000)
);

-- Datasource users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    login VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    datasource_name VARCHAR(255) NOT NULL
);

-- Create index for users table (H2 compatible syntax)
CREATE INDEX IF NOT EXISTS idx_login_datasource ON users(login, datasource_name);