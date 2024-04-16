package ru.poltoranin.hhbot.component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import ru.poltoranin.hhbot.dto.AppTokenDTO;
import ru.poltoranin.hhbot.dto.UserTokenDTO;
import ru.poltoranin.hhbot.util.Constant;


@Component
@ConfigurationProperties(prefix = "env")
@Getter
@Setter
public class EnvProperties {
    private String envPath;
    private String coveringLetterPath;
    private String clientId;
    private String clientSecret;
    private String appToken;
    private String userAuthCode;
    private String userAccessToken;
    private String userRefreshToken;
    private String appEmail;
    private String userAgent;
    private String resumeId;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private Constant constant;

    private String vacancyText;
    private String vacancyExperience;
    private String vacancyEmployment;
    private String vacancySchedule;
    private String vacancyArea;
    private String vacancyCurrency;
    private String vacancySalary;
    private String vacancyOnlyWithSalary;
    private String vacancyPeriod;



    @PostConstruct
    private void check() {
        checkEnvs();
    }

    private void checkEnvs() {
        if (clientId == null || clientId.equals("")) {
            System.out.println(
                    "Перейди по ссылке https://dev.hh.ru/admin, найди Client ID и добавь его в CLIENT_ID в .env.");
            throw new NullPointerException("Need set CLIENT_ID in environment file");
        }
        if (clientSecret == null || clientSecret.equals("")) {
            System.out.println(
                    "Перейди по ссылке https://dev.hh.ru/admin, найди Client Secret и добавь его в CLIENT_SECRET в .env.");
            throw new NullPointerException("Need set CLIENT_SECRET in environment file");
        }
        if (appEmail == null || appEmail.equals("")) {
            System.out.println("Укажи свой email в APP_EMAIL в .env.");
            throw new NullPointerException("Need set APP_EMAIL in environment file");
        }

        if (userAgent == null || userAgent.equals("")) {
            System.out.println("Укажи user agent в USER_AGENT в .env.");
            throw new NullPointerException("Need set USER_AGENT in environment file");
        }

        if (resumeId == null || resumeId.equals("")) {
            System.out.println(
                    "Укажи id вакансии в RESUME_ID в .env. Пример - набор символов после последнего слеша: https://spb.hh.ru/resume/fe83c572ff0bfae59c0039ed1f4131496c744b");
            throw new NullPointerException("Need set RESUME_ID in environment file");
        }

        if (appToken == null || appToken.equals("")) {
            setAppToken();
        }

    }


    public void setUserAccessTokenAndUserRefreshToken() {
        String url =
                "https://api.hh.ru/token?client_id=%s&client_secret=%s&code=%s&grant_type=authorization_code&redirect_uri=%s"
                        .formatted(getClientId(), getClientSecret(), getUserAuthCode(),
                                constant.getRedirectUrl());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("User-Agent", userAgent);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<UserTokenDTO> response =
                restTemplate.postForEntity(url, requestEntity, UserTokenDTO.class);

        setUserAccessToken(response.getBody().getAccessToken());
        setUserRefreshToken(response.getBody().getRefreshToken());
    }



    public void sendLinkForGetCode() {
        var url = "https://hh.ru/oauth/authorize?response_type=code&client_id=%s&redirect_uri=%s"
                .formatted(clientId, constant.getRedirectUrl());
        System.out.println("Click on link: %s".formatted(url));
        // TODO send email
    }

    /**
     * Установка токена приложения в переменные окружения и запись в .env.
     */
    private void setAppToken() {
        appToken = generateAppToken(clientId, clientSecret);
        addVariableToEnvFile(String.format("APP_TOKEN=%s", appToken));
    }

    /**
     * Генерация токена приложения.
     *
     * @param clientId ID приложения (<a href="https://dev.hh.ru/admin">link</a>)
     * @param clientSecret Секретная строка приложения (<a href="https://dev.hh.ru/admin">link</a>)
     * @return токен приложения
     */
    public String generateAppToken(String clientId, String clientSecret) {
        String url = constant.getBaseUrl() + "/oauth/token";
        String payload =
                String.format("client_id=%s&client_secret=%s&grant_type=client_credentials",
                        getClientId(), getClientSecret());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("User-Agent", userAgent);

        HttpEntity<String> requestEntity = new HttpEntity<>(payload, headers);
        ResponseEntity<AppTokenDTO> response =
                restTemplate.postForEntity(url, requestEntity, AppTokenDTO.class);
        var token = response.getBody().getAccessToken();
        return token;
    }

    /**
     * Запись переменной в .env
     *
     * @param variable Переменная вида MY_KEY=value
     */
    private void addVariableToEnvFile(String variable) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(envPath, true))) {
            writer.newLine();
            writer.append(variable);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
