package ar.edu.utn.frc.tup.lciii.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchDto {
    private Long id;
    private LocalDate date;
    private List<TeamResultsDto> teams;
    private Integer stadium;
    private String pool;
}
