package ru.poltoranin.hhbot.dto;

import java.util.HashMap;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NegotiationDTO {

    private String requestId;
    private String badArgument;
    private String description;
    private List<HashMap<String, Object>> badArguments;
    private List<HashMap<String, Object>> errors;

}
