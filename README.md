# headhunter-bot-java
Автоотклики на вакансии в Headhunter.
Написано на фреймворке Spring Boot.

## Как запустить
1. Скачать репозиторий
    ```bash
    git clone git@github.com:NankuF/headhunter-bot-java.git
    ```
2. Создать файл `.env` и положить его в `./secret/.env`
3. Написать свое сопроводительное письмо `covering_letter.txt`. Лежит в `./secret/covering_letter.txt`
4. В `.env` указать обязательные переменные
5. В `.env` указать переменные для фильтрации вакансий
6. Запустить приложение либо в докере, либо в консоли
   ```bash
   # Docker
   # in headhunter-bot-java directory
   cd hh-bot/&& ./gradlew build && cd .. && docker build -t hh-bot-java . && docker run -p 8080:8080 --name hh-bot-java -v ./secret:/app/secret --restart always hh-bot-java
   ```
   ```bash
    # console
    cd ./hh-bot && ./gradlew build && cd .. && java -jar ./hh-bot/build/libs/hh-bot-0.0.1-SNAPSHOT.jar
   ```

## Переменные окружения
Список переменных также указан в `example.env`

### Обязательные переменные
`ENV_PATH=./secret/.env`
`COVERING_LETTER_PATH=./secret/covering_letter.txt`
`CLIENT_ID=взять в личном кабинете`<br>
`CLIENT_SECRET=взять в личном кабинете`<br>
`APP_EMAIL=ваш емайл`<br>
`RESUME_ID=id вашего резюме`, например `fe83c572ff0bfae59c0039ed1f4131496c744b` из https://spb.hh.ru/resume/fe83c572ff0bfae59c0039ed1f4131496c744b<br>
`USER_AGENT=myJavaApp/v1 (${APP_EMAIL})`

### Переменные для фильтрации вакансий
Значения могут быть пустыми, например `VACANCY_EXPERIENCE=`
#### Значения можно найти в справочнике /dictionaries
***название вакансии<br>***
`VACANCY_TEXT=java`<br>

***"noExperience" - без опыта<br>
"between1And3" - с опытом от 1 до 3 лет<br>
"between3And6" - с опытом от 3 до 6 лет<br>
"moreThan6" - с опытом более 6 лет<br>***
`VACANCY_EXPERIENCE=between1And3`<br>

***"full" - полная занятость<br>
"part" - частичная<br>
"project" - проектная<br>
"volunteer" - волонтер<br>
"probation" - стажировка <br>***
`VACANCY_EMPLOYMENT=full`<br>

***"fullDay" - полный рабочий день<br>
"shift" - сменный график<br>
"flexible" - гибкий график<br>
"remote" - удаленная работа<br>
"flyInFlyOut" - вахтовый метод<br>***
`VACANCY_SCHEDULE=remote`<br>
`VACANCY_CURRENCY=RUR`<br>

***Если `VACANCY_ONLY_WITH_SALARY=false` и `VACANCY_SALARY=150000`, то покажет список вакансий без зарплаты либо с зарплатной вилкой с медианой в 150000<br>***
`VACANCY_SALARY=150000`<br>

***показать вакансии только с указанной зарплатой***
`VACANCY_ONLY_WITH_SALARY=true`<br>

***показать вакансии за последние 30 дней***<br>
`VACANCY_PERIOD=30`<br>
#### Значения можно найти в справочнике /areas
***необходимо указать id страны, города и т.д.***<br>
0 - Россия<br>
1 - Москва<br>
2 - Санкт-Петербург...<br>
`VACANCY_AREA=2`<br>
