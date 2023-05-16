# Database

## 1. Mysql container
```
docker run --name auth-mysql -p3306:3306 -e MYSQL_DATABASE=core-mysql -e MYSQL_ROOT_PASSWORD=root -v auth_mysql_datadir:/var/lib/mysql --restart always -d mysql:8.0.33
```

### setup empty mysql database:
```
docker exec -it isubmit-mysql sh -c "mysql -uroot -proot125p -e 'create database core-mysql;'"
```

## 2. Postgres container
```
docker run --name submission-postgres -p5432:5432 -e POSTGRES_DB=submission-postgres -e POSTGRES_PASSWORD=postgres -v core_postgres_pgdata:/var/lib/postgresql/data --restart always -d postgres:15.3
```
setup empty postgres database:
```
docker exec -it submission-postgres sh -c "createdb -U postgres auth-postgres;"
```


# 2. Mailhog
```
docker run --name mango-mailhog -p1025:1025 -p8025:8025 --restart always -d mailhog/mailhog:latest
```

Check mailhog:
On Windows, you can send an email with PowerShell by executing the following code:
```
Send-MailMessage -From "no-replay-security@mango.dqtri.com" -To "mango@mango.dqtri.com" -Subject "Hello, Xoai!" -Body "I love you 3000!" -SmtpServer "localhost" -Port 1025
```



# 3. Ldap

```
docker run --detach --rm --name openldap \
  -p1389:1389 -p1636:1636 \
  --env LDAP_ADMIN_USERNAME=admin \
  --env LDAP_ADMIN_PASSWORD=admin\
  --env LDAP_USERS=custom \
  --env LDAP_PASSWORDS=custom \
  -v mango_openldap_data:/bitnami/openldap \
  bitnami/openldap:latest
```



