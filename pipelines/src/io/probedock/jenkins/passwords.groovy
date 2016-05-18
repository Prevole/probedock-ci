package io.probedock.jenkins

/**
 * Define the password names
 */
def POSTGRESSQL_PASSWORD_NAME = env.PROBEDOCK_ENV + '-PostgreSQLRoot'
def PROBEDOCK_DB_PASSWORD_NAME = env.PROBEDOCK_ENV + '-ProbeDockPostgreSQL'

/**
 * Define the password names in Docker Compose env vars
 */
def DOCKER_POSTGRESQL_PASSWORD_VARNAME = 'POSTGRES_PASSWORD'
def DOCKER_PROBEDOCK_DB_PASSWORD_VARNAME = 'PROBEDOCK_DATABASE_PASSWORD'

return this