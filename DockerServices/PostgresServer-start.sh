
docker pull postgres:11.2

docker run -p 15432:5432     \
    --name PostgresDatabaseSpringCloudSample   \
    -e POSTGRES_PASSWORD=rootroot   \
    -d postgres:11.2
    #-v LOCAL_FOLDER:/var/libpostgresql/data  \

docker logs -f PostgresDatabaseSpringCloudSample

