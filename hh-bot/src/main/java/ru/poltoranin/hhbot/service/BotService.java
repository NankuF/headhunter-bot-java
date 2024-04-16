package ru.poltoranin.hhbot.service;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import lombok.Data;
import ru.poltoranin.hhbot.component.EnvProperties;
import ru.poltoranin.hhbot.dto.NegotiationDTO;
import ru.poltoranin.hhbot.dto.VacancyResponseDTO;
import ru.poltoranin.hhbot.util.Constant;

@Service
@Data
public class BotService {
    private boolean userOpenLink = false;
    private HttpHeaders httpHeaders = new HttpHeaders();

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private EnvProperties envs;
    @Autowired
    private Constant constant;

    private String readFileInResources(String path) throws IOException {
        Path p = Paths.get(path);
        List<String> strings = Files.readAllLines(p);
        var message = String.join("\n", strings);
        return message;
    }

    public void run() throws InterruptedException, IOException {
        envs.sendLinkForGetCode();

        while (!userOpenLink) {
            Thread.sleep(10);
        }
        System.out.println("USER OPEN LINK");
        System.out.println("Set UserAuthCode");
        envs.setUserAccessTokenAndUserRefreshToken();
        System.out.println("Set UserAccessToken");

        httpHeaders.add("Authorization", "Bearer %s".formatted(envs.getUserAccessToken()));
        httpHeaders.add("User-Agent", envs.getUserAgent());

        while (true) {
            var vacancies = getVacanciesAuth().getItems();
            responseToVacancy(vacancies);
            System.out.println("Откликнулся на все вакансии");
            System.out.println("Засыпаю на 24 часа");
            Thread.sleep(constant.getDayInMilliseconds());

        }
    }

    /**
     * Получение вакансий для автооткликов. Параметры запроса считываются из переменных окружения.
     *
     * @return ответ сервера, в котором items - это список вакансий.
     */
    public VacancyResponseDTO getVacanciesAuth() {
        String url = constant.getVacanciesUrl();
        UriComponentsBuilder urlTemplate = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("per_page", "100").queryParam("text", envs.getVacancyText());
        if (!envs.getVacancyExperience().equals("")) {
            urlTemplate.queryParam("experience", envs.getVacancyExperience());
        }
        if (!envs.getVacancyEmployment().equals("")) {
            urlTemplate.queryParam("employment", envs.getVacancyEmployment());
        }
        if (!envs.getVacancySchedule().equals("")) {
            urlTemplate.queryParam("schedule", envs.getVacancySchedule());
        }
        if (!envs.getVacancyArea().equals("")) {
            urlTemplate.queryParam("area", envs.getVacancyArea());
        }
        if (!envs.getVacancyCurrency().equals("")) {
            urlTemplate.queryParam("currency", envs.getVacancyCurrency());
        }
        if (!envs.getVacancySalary().equals("")) {
            urlTemplate.queryParam("salary", envs.getVacancySalary());
        }
        if (!envs.getVacancyOnlyWithSalary().equals("")) {
            urlTemplate.queryParam("only_with_salary", envs.getVacancyOnlyWithSalary());
        }
        if (!envs.getVacancyPeriod().equals("")) {
            urlTemplate.queryParam("period", envs.getVacancyPeriod());
        }
        String resultUrl = urlTemplate.encode().toUriString();

        httpHeaders.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(httpHeaders);

        ResponseEntity<VacancyResponseDTO> responseEntity =
                restTemplate.exchange(resultUrl, HttpMethod.GET, entity, VacancyResponseDTO.class);
        VacancyResponseDTO response = responseEntity.getBody();
        return response;
    }

    /**
     * Получить словари.
     *
     * @return словари, в которых хранятся id параметров для вакансий.
     */
    public Object getDictionaries() {
        String url = constant.getDictionariesUrl();
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(url).encode().toUriString();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(httpHeaders);
        ResponseEntity<Object> responseEntity =
                restTemplate.exchange(urlTemplate, HttpMethod.GET, entity, Object.class);
        Object response = responseEntity.getBody();
        return response;
    }

    /**
     * Получить словари для выбора территории поиска.
     *
     * @return словари, в которых хранятся id параметров для вакансий.
     */
    public Object getAreas() {
        String url = constant.getAreasUrl();
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(url).encode().toUriString();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(httpHeaders);
        ResponseEntity<Object> responseEntity =
                restTemplate.exchange(urlTemplate, HttpMethod.GET, entity, Object.class);
        Object response = responseEntity.getBody();
        return response;
    }

    /**
     * Получение списка вакансий для API. Параметры вопроса можно получить, запросив эндпоинт
     * /dictionaries.
     *
     * @param params параметры запроса указываются вручную.
     * @return ответ сервера, в котором items - это список вакансий.
     */
    public VacancyResponseDTO getVacancies(MultiValueMap<String, String> params) {
        String url = constant.getVacanciesUrl();
        var uri = UriComponentsBuilder.fromHttpUrl(url).queryParams(params).build().toUri();

        ResponseEntity<VacancyResponseDTO> responseEntity =
                restTemplate.getForEntity(uri, VacancyResponseDTO.class);

        VacancyResponseDTO response = responseEntity.getBody();
        return response;
    }

    /**
     * Отклинуться на вакансии.
     *
     * @param vacancies список вакансий.
     * @throws IOException
     * @throws InterruptedException
     */
    public void responseToVacancy(List<HashMap<String, Object>> vacancies)
            throws IOException, InterruptedException {
        String url = constant.getResponseToVacancyUrl();
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(url).encode().toUriString();

        httpHeaders.set(HttpHeaders.ACCEPT, MediaType.MULTIPART_FORM_DATA_VALUE);
        // TODO add blacklist
        for (HashMap<String, Object> hashMap : vacancies) {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            var msg = readFileInResources(envs.getCoveringLetterPath());
            System.out.println("MESSAGE=%s".formatted(msg));
            formData.add("message", msg);
            // formData.add("message", "пишу письмо вам тратата");
            formData.add("resume_id", envs.getResumeId());
            formData.add("vacancy_id", (String) hashMap.get("id"));
            HttpEntity<?> entity = new HttpEntity<>(formData, httpHeaders);

            ResponseEntity<NegotiationDTO> responseEntity = null;
            try {
                System.out.println("ОТКЛИК УСПЕШНО ВЫПОЛНЕН");
                responseEntity = restTemplate.exchange(urlTemplate, HttpMethod.POST, entity,
                        NegotiationDTO.class);
            } catch (HttpClientErrorException e) {
                if (e.getMessage().contains("limit_exceeded")) {
                    System.out.println("Превышен лимит количества откликов.");
                    break;
                }
                if (e.getMessage().contains("vacancy_not_found")) {
                    System.out.println("Вакансия не найдена.");
                    continue;
                }
                if (e.getMessage().contains("resume_not_found")) {
                    throw new NullPointerException(
                            "Резюме из отклика/приглашения скрыто, удалено или не найдено.");
                    // TODO Отправить юзеру сообщение.
                }
                if (e.getMessage().contains("resource_policy_violation")) {
                    throw new NullPointerException("Отклик нарушает правила пользования сервисом.");
                    // TODO Отправить юзеру сообщение.
                }
                if (e.getMessage().contains("inappropriate_language")) {
                    throw new NullPointerException("Отклик содержит нецензурную лексику.");
                    // TODO Отправить юзеру сообщение.
                }
            }

            if (responseEntity != null) {
                // url = /negotiations/<id отклика>
                URI response = responseEntity.getHeaders().getLocation();
                if (response != null) {
                    System.out.println("Отклик на вакансию %s %s %s - выполнен".formatted(
                            hashMap.get("name"), hashMap.get("id"), hashMap.get("alternate_url")));
                } else {
                    System.out.println("Не удалось откликнуться на вакансию");
                }

                Thread.sleep(1000, 0);
                // // FIXME remove after development
                // break;
            }
        }
    }
}
