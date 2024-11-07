package ar.edu.utn.frc.tup.lciii.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamDto {
    private Long id;
    private String name;
    private String country;
    @JsonProperty(value = "world_ranking")
    private Integer worldRanking;
    private String pool;
}
