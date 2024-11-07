package ar.edu.utn.frc.tup.lciii.dtos.common;


import ar.edu.utn.frc.tup.lciii.models.TeamStanding;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PoolResultsDto {
    private String poolId;
    private List<TeamStanding> teams;
}
