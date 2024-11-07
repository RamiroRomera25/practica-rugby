package ar.edu.utn.frc.tup.lciii.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TeamStanding {
    private Long teamId;
    private String teamName;
    private String country;
    private Integer matchesPlayed = 0;
    private Integer wins = 0;
    private Integer draws = 0;
    private Integer losses = 0;
    private Integer pointsFor = 0;
    private Integer pointsAgainst = 0;
    private Integer pointsDifferential = 0;
    private Integer triesMade = 0;
    private Integer bonusPoints = 0;
    private Integer points = 0;
    private Integer totalYellowCards = 0;
    private Integer totalRedCards = 0;
}
