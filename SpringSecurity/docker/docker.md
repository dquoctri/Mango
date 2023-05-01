# 1. Mysql container

```
docker run --name mango-mysql -p3306:3306 -e MYSQL_ROOT_PASSWORD=root -v mango_mysql_datadir:/var/lib/mysql --restart always -d mysql:8.0.29
```

setup empty mysql database:
```
docker exec -it mango-mysql sh -c "mysql -uroot -proot -e 'create database mango;'"
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




