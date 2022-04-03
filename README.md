# hibernate-envers-rollback-poc
POC to make rollbacks based on hibernate envers project

### * start Postgres in docker before you launch the application
`docker run --rm --name rollbackdb -e POSTGRES_DB=rollbackdb -e POSTGRES_USER=sa -e POSTGRES_PASSWORD=sa -p 5432:5432 -d postgres`


