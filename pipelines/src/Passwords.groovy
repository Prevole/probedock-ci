

/**
 * Define the password names
 */
this.POSTGRESSQL_PASSWORD_NAME = env.PROBEDOCK_ENV + '-PostgreSQLRoot'
this.PROBEDOCK_DB_PASSWORD_NAME = env.PROBEDOCK_ENV + '-ProbeDockPostgreSQL'

/**
 * Define the password names in Docker Compose env vars
 */
this.DOCKER_POSTGRESQL_PASSWORD_VARNAME = 'POSTGRES_PASSWORD'
this.DOCKER_PROBEDOCK_DB_PASSWORD_VARNAME = 'PROBEDOCK_DATABASE_PASSWORD'

return this