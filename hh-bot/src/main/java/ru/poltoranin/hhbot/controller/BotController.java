package ru.poltoranin.hhbot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.poltoranin.hhbot.component.EnvProperties;
import ru.poltoranin.hhbot.dto.VacancyResponseDTO;
import ru.poltoranin.hhbot.service.BotService;


@RestController
public class BotController {

    @Autowired
    private BotService botService;

    @Autowired
    private EnvProperties envs;


    @GetMapping("/vacancies")
    @ResponseStatus(HttpStatus.OK)
    public VacancyResponseDTO getVacancies(
            @RequestParam(required = false) MultiValueMap<String, String> params) {
        return botService.getVacancies(params);
    }

    @GetMapping("/dictionaries")
    @ResponseStatus(HttpStatus.OK)
    // @ResponseBody
    public Object getDictionaries() {
        return botService.getDictionaries();
    }

    @GetMapping("/areas")
    @ResponseStatus(HttpStatus.OK)
    // @ResponseBody
    public Object getAreas() {
        return botService.getAreas();
    }

    @GetMapping("/start")
    @ResponseStatus(HttpStatus.OK)
    public void getCode(@RequestParam String code) {
        envs.setUserAuthCode(code);
        botService.setUserOpenLink(true);
    }


}
