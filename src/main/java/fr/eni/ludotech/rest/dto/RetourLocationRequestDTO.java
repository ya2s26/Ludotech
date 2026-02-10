package fr.eni.ludotech.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class RetourLocationRequestDTO {
    private List<String> codeBarres;
}
