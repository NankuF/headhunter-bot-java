package ru.poltoranin.hhbot.dto;

import java.util.HashMap;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VacancyResponseDTO {

    private Integer page;
    private Integer pages;
    @JsonProperty("per_page")
    private Integer perPage;
    private Integer found;
    private List<HashMap<String, Object>> items;

}
