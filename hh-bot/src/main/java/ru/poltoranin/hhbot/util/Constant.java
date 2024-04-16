package ru.poltoranin.hhbot.util;

import org.springframework.stereotype.Component;
import lombok.Getter;


@Component
@Getter
public class Constant {
    private final String baseUrl = "https://hh.ru";
    private final String apiUrl = "https://api.hh.ru";
    private final String redirectUrl = "http://localhost:8080/start";
    private final String responseToVacancyUrl = "https://api.hh.ru/negotiations";
    private final String vacanciesUrl = "https://api.hh.ru/vacancies/";
    private final String dictionariesUrl = "https://api.hh.ru/dictionaries";
    private final String areasUrl = "https://api.hh.ru/areas";
    private final Integer dayInMilliseconds = 60 * 60 * 24 * 1000;

}
