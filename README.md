# hibernate-envers-rollback-poc
POC to make rollbacks based on hibernate envers project

### * When running localy start Postgres in docker before you launch the application
`docker run --rm --name rollbackdb -e POSTGRES_DB=rollbackdb -e POSTGRES_USER=sa -e POSTGRES_PASSWORD=sa -p 5432:5432 -d postgres`
for psql
`docker run -it --rm --link rollbackdb postgres psql -h rollbackdb -U sa -d rollbackdb`

### * to run docker-compose
`gradle jibDockerBuild --image=enverspoc`
`docker-compose up -d`
`docker run -it --rm --network=hibernate-envers-rollback-poc_default --link hibernate-envers-rollback-poc_rollbackdb_1 postgres psql -h rollbackdb -U sa -d rollbackdb`
