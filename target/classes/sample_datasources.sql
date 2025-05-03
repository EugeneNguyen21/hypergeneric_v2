-- Sample datasource configurations
INSERT INTO database_configs (ds_name, username, password, driver, db_type, connection_url, license)
VALUES 
('Sample MySQL DB', 'mysql_user', 'mysql_password', 'com.mysql.cj.jdbc.Driver', 'MYSQL', 'jdbc:mysql://localhost:3306/sample_db', NULL),
('Sample PostgreSQL DB', 'postgres_user', 'postgres_password', 'org.postgresql.Driver', 'POSTGRESQL', 'jdbc:postgresql://localhost:5432/sample_db', NULL),
('Sample Oracle DB', 'oracle_user', 'oracle_password', 'oracle.jdbc.OracleDriver', 'ORACLE', 'jdbc:oracle:thin:@localhost:1521:orcl', NULL),
('Sample SQL Server DB', 'sqlserver_user', 'sqlserver_password', 'com.microsoft.sqlserver.jdbc.SQLServerDriver', 'SQLSERVER', 'jdbc:sqlserver://localhost:1433;databaseName=sample_db', NULL);