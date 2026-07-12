docker run -d \
  --name postgres-db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=bibliotecadb \
  -p 5432:5432 \
  -v postgres_data:/var/lib/postgresql/data \
  postgres:15

  --verifica el contenedor
  docker ps

  --entra al contenedor
  docker exec -it postgres-db bash

  --Entrar a postgresql
  psql -U postgres

  --ver bases de datos
  \l

  --conectarte a la base de datos
  \c bibliotecadb

  --salir de postgres
  \q

  spring.datasource.url=jdbc:postgresql://localhost:5432/bibliotecadb
  spring.datasource.username=postgres
  spring.datasource.password=postgres
  spring.datasource.driver-class-name=org.postgresql.Driver

