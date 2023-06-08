# SMA
Social media api

Для начала работы с API необходимо зарегистирироваться и получить JWT-токен. Сделать это нужно при помощи энпоинта: ```/auth/register```. Запрос регистрации должен содержать в себе следущую информацию:
```
{
  "username":"someuser",
  "email":"someemail@mail.ru",
  "password":"0000"
}
```

В ответ будет получен JWT-токен, который потом необходимо будет использовать в качестве header'a для Bearer авторизации:
```
{
    "access_token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzb21ldXNlciIsImlhdCI6MTY4NjI1NDAyMywiZXhwIjoxNjg2MjY0MDIzfQ.zFS3nCFEfTVSfgx7-LOD-GJU_BV96cgiU_FcahdPwh0"
}
```

Для входа в систему или же повторной генерации токена, если он устарел, предназначен эндпоинт: ```localhost:8080/auth/login```.
Запрос для входа в систему имеет структуру:
```
{
    "username":"someuser",
    "password":"0000"
}
```

В ответ также будет получен новый токен:
```
{
    "access_token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmYWlyeSIsImlhdCI6MTY4NjI1NDI4NywiZXhwIjoxNjg2MjY0Mjg3fQ.MvqYdHybZYLWCgz_YUFV4S2_fBAHE64kQ1he4jLHY6E"
}
```


Краткое описание остальных эндпоинтов доступно по: /swagger-ui.html 
Полный адрес, в случае, если приложение запущено на локальной машине: http://localhost:8080/swagger-ui.html





# SMA
Social media API

To start working with the API, you need to register and obtain a JWT token. To do this, use the following endpoint: `/auth/register`. The registration request should include the following information:

```
{
   "username": "someuser",
   "email": "someemail@mail.ru",
   "password": "0000"
}
```

In response, a JWT token will be received, which then should be used as a header for Bearer authorization:
```
{
     "access_token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzb21ldXNlciIsImlhdCI6MTY4NjI1NDAyMywiZXhwIjoxNjg2MjY0MDIzfQ.zFS3nCFEfTVSfgx7-LOD-GJU_BV96cgiU_FcahdPwh 0"
}
```

To login or regenerate a token if it is out of date, you need to use the following endpoint:
```
localhost:8080/auth/login
```

The login request has the structure:
```
{
     "username":"someuser",
     "password":"0000"
}
```

In a response new token will be received:
```
{
     "access_token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmYWlyeSIsImlhdCI6MTY4NjI1NDI4NywiZXhwIjoxNjg2MjY0Mjg3fQ.MvqYdHybZYLWCgz_YUFV4S2_fBAHE64kQ1he4jLHY6E "
}
```

A brief description of the rest of the endpoints is available at: /swagger-ui.html
Full address, if the application is running on the local machine: http://localhost:8080/swagger-ui.html
