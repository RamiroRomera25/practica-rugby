package ar.edu.utn.frc.tup.lciii.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamResultsDto {
    private Long id;
    private Integer points;
    private Integer tries;
    @JsonProperty(value = "yellow_cards")
    private Integer yellowCards;
    @JsonProperty(value = "red_cards")
    private Integer redCards;
}
