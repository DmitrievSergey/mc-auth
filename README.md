# mc-auth

## Как развернуть
1. Фронт:
   1. Из телеграмм канала забираем фронтенд [Фронт](https://t.me/c/2482837105/75)
   2. В vue.config.js меняем порт на требуемый
   3. Запускаем командой ```yarn serve --ignore-engines```
2. База данных
   1. из директории docker ```docker compose up -d```
3. Апп
   1. Если есть желание посмотреть восстановление пароля в application.yml надо заполнить spring.mail.username и spring.mail.password
   
