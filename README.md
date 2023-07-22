# Mango
Study spring security in best practic

# SafeguardSpring
Spring Security is a powerful and highly customizable authentication and access-control framework. It is the de-facto standard for securing Spring-based applications​

# [Docker](https://docs.docker.com/get-started/overview)
> Docker is an open platform for developing, shipping, and running applications. Docker enables you to separate your applications from your infrastructure so you can deliver software quickly. With Docker, you can manage your infrastructure in the same ways you manage your applications. By taking advantage of Docker’s methodologies for shipping, testing, and deploying code quickly, you can significantly reduce the delay between writing code and running it in production.

# [Docker Compose](https://docs.docker.com/compose/)
> Compose is a tool for defining and running multi-container Docker applications. With Compose, you use a YAML file to configure your application’s services. Then, with a single command, you create and start all the services from your configuration.

# Prerequisities
## Docker and docker-compos
Docker and docker-compose are required in order to run this app successfully
* docker >= 19.03.0+
* docker-compose

You can download and install Docker on multiple platforms. Refer to the [following section](https://docs.docker.com/get-docker/) and choose the best installation path for you.

Check the version of docker to make sure docker is installed

```
docker --version
```
> Docker version 20.10.7, build f0df350

# Database
## 2. Postgres container
```
docker run --name submission-postgres -p5432:5432 -e POSTGRES_DB=submission-postgres -e POSTGRES_PASSWORD=postgres -v core_postgres_pgdata:/var/lib/postgresql/data --restart always -d postgres:15.3
```

#### setup empty postgres database:
```
docker exec -it submission-postgres sh -c "createdb -U postgres auth-postgres;"
```

## JDK 17
[Download](https://www.oracle.com/java/technologies/downloads/#java17) jdk-17_windows-x64_bin.msi

## Postman
Postman is available for Windows 7 and later.

[Download](https://www.postman.com/downloads/) the latest Postman version.
Select and run the .exe file to install Postman.

IntelliJ IDEA Community Edition
The IDE for pure Java and Kotlin development

start this project with IntelliJ
```
https://github.com/dquoctri/SafeguardSpring.git

```