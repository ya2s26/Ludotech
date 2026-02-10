package fr.eni.ludotech.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExemplaireRequestDTO {
    private Integer jeuId;
    private String reference;
    private String codeBarre;
    private boolean louable = true;
}
