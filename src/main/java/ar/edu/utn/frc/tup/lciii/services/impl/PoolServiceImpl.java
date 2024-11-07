package ar.edu.utn.frc.tup.lciii.services.impl;

import ar.edu.utn.frc.tup.lciii.client.MatchDto;
import ar.edu.utn.frc.tup.lciii.client.MatchRestClient;
import ar.edu.utn.frc.tup.lciii.client.TeamDto;
import ar.edu.utn.frc.tup.lciii.client.TeamRestClient;
import ar.edu.utn.frc.tup.lciii.client.TeamResultsDto;
import ar.edu.utn.frc.tup.lciii.dtos.common.PoolDto;
import ar.edu.utn.frc.tup.lciii.dtos.common.PoolResultsDto;
import ar.edu.utn.frc.tup.lciii.models.Team;
import ar.edu.utn.frc.tup.lciii.models.TeamStanding;
import ar.edu.utn.frc.tup.lciii.services.PoolService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class PoolServiceImpl implements PoolService {

    @Autowired
    private TeamRestClient teamRestClient;

    @Autowired
    private MatchRestClient matchRestClient;

    @Autowired
    private ModelMapper modelMapper;

    private static final String FALLBACK_METHOD = "fallback";

    private void fallback(Throwable t) {
        throw new HttpClientErrorException(HttpStatusCode.valueOf(503), "CIRCUIT BREAKER ACTIVADO.");
    }


    @CircuitBreaker(name = "circuitBreakerParcial", fallbackMethod = FALLBACK_METHOD)
    public List<PoolDto> getAllPools() {
        TeamDto[] listTeams = teamRestClient.getTeams().getBody();
        List<PoolDto> poolDtoList = new ArrayList<>();

        poolDtoList.add(new PoolDto("A", new ArrayList<>()));
        poolDtoList.add(new PoolDto("B", new ArrayList<>()));
        poolDtoList.add(new PoolDto("C", new ArrayList<>()));
        poolDtoList.add(new PoolDto("D", new ArrayList<>()));
        
        for (TeamDto team : listTeams) {
            switch (team.getPool()) {
                case "A":
                    poolDtoList.get(0).getTeams().add(modelMapper.map(team, Team.class));
                    break;
                case "B":
                    poolDtoList.get(1).getTeams().add(modelMapper.map(team, Team.class));
                    break;
                case "C":
                    poolDtoList.get(2).getTeams().add(modelMapper.map(team, Team.class));
                    break;
                case "D":
                    poolDtoList.get(3).getTeams().add(modelMapper.map(team, Team.class));
                    break;
            }
        }
        return poolDtoList;
    }

    @CircuitBreaker(name = "circuitBreakerParcial", fallbackMethod = FALLBACK_METHOD)
    public PoolDto getPool(String group) {
        TeamDto[] listTeams = teamRestClient.getTeams().getBody();
        PoolDto poolDto = new PoolDto(group, new ArrayList<>());

        for (TeamDto team : listTeams) {
            if (Objects.equals(team.getPool(), group)) {
                poolDto.getTeams().add(modelMapper.map(team, Team.class));
            }
        }
        return poolDto;
    }

    private List<MatchDto> getAllMatches(String pool) {
        List<MatchDto> matchDtos = new ArrayList<>();
        MatchDto[] matchFromRest = matchRestClient.getAllMatches().getBody();
        for (MatchDto match : matchFromRest) {
            if (Objects.equals(match.getPool(), pool)) {
                matchDtos.add(match);
            }
        }
        return matchDtos;
    }

    @CircuitBreaker(name = "circuitBreakerParcial", fallbackMethod = FALLBACK_METHOD)
    public PoolResultsDto calculatePoints(String pool) {
        PoolResultsDto poolResultsDto = new PoolResultsDto(pool, new ArrayList<>());
        PoolDto poolDto = getPool(pool);

        for (Team team : poolDto.getTeams()) {
            TeamStanding teamStanding = new TeamStanding();

            teamStanding.setTeamId(team.getId());
            teamStanding.setTeamName(team.getName());
            teamStanding.setCountry(team.getCountry());

            poolResultsDto.getTeams().add(teamStanding);
        }


        for (MatchDto match : getAllMatches(pool)) {
            calculateWhoWins(match, poolResultsDto);
        }
        return poolResultsDto;
    }

    private void calculateWhoWins(MatchDto match, PoolResultsDto poolResultsDto) {
        if (match.getTeams().get(0).getPoints() > match.getTeams().get(1).getPoints()) {
            TeamStanding teamWinner = getTeamsOfResult(poolResultsDto, match.getTeams().get(0).getId());
            TeamStanding teamLoser = getTeamsOfResult(poolResultsDto, match.getTeams().get(1).getId());

            calculateStandings(teamWinner, teamLoser, match.getTeams().get(0), match.getTeams().get(1), false);
            verifyBonusPoints(teamWinner, teamLoser, match.getTeams().get(0), match.getTeams().get(1), false);
            calculateMiscellaneous(teamWinner, match.getTeams().get(0));
            calculateMiscellaneous(teamLoser, match.getTeams().get(1));
        } else if ((match.getTeams().get(0).getPoints() < match.getTeams().get(1).getPoints())) {
            TeamStanding teamWinner = getTeamsOfResult(poolResultsDto, match.getTeams().get(1).getId());
            TeamStanding teamLoser = getTeamsOfResult(poolResultsDto, match.getTeams().get(0).getId());

            calculateStandings(teamWinner, teamLoser, match.getTeams().get(1), match.getTeams().get(0), false);
            verifyBonusPoints(teamWinner, teamLoser, match.getTeams().get(1), match.getTeams().get(0), false);
            calculateMiscellaneous(teamWinner, match.getTeams().get(1));
            calculateMiscellaneous(teamLoser, match.getTeams().get(0));
        } else {
            TeamStanding teamDraw1 = getTeamsOfResult(poolResultsDto, match.getTeams().get(0).getId());
            TeamStanding teamDraw2 = getTeamsOfResult(poolResultsDto, match.getTeams().get(1).getId());

            calculateStandings(teamDraw1, teamDraw2, match.getTeams().get(0), match.getTeams().get(1), true);
            verifyBonusPoints(teamDraw1, teamDraw2, match.getTeams().get(0), match.getTeams().get(1), true);
            calculateMiscellaneous(teamDraw1, match.getTeams().get(0));
            calculateMiscellaneous(teamDraw2, match.getTeams().get(1));
        }
    }

    private void calculateMiscellaneous(TeamStanding team, TeamResultsDto teamResult) {
        team.setPointsDifferential(team.getPointsFor() - team.getPointsAgainst());
        team.setTotalYellowCards(team.getTotalYellowCards() + teamResult.getYellowCards());
        team.setTotalRedCards(team.getTotalRedCards() + teamResult.getRedCards());
        team.setMatchesPlayed(team.getMatchesPlayed() + 1);
        team.setPoints(team.getWins() * 4 + team.getDraws() + team.getBonusPoints());
    }

    private void calculateStandings(TeamStanding teamWinner, TeamStanding teamLoser,
                                    TeamResultsDto resultTeamWinner, TeamResultsDto resultTeamLoser,
                                    boolean isDraw) {
        if (isDraw) {
            teamWinner.setDraws(teamWinner.getDraws() + 1);
            teamLoser.setDraws(teamLoser.getDraws() + 1);

            teamWinner.setPointsFor(teamWinner.getPointsFor() + resultTeamWinner.getPoints());
            teamWinner.setPointsAgainst(teamWinner.getPointsAgainst() + resultTeamLoser.getPoints());

            teamLoser.setPointsFor(teamLoser.getPointsFor() + resultTeamLoser.getPoints());
            teamLoser.setPointsAgainst(teamLoser.getPointsAgainst() + resultTeamWinner.getPoints());
        } else {
            teamWinner.setWins(teamWinner.getWins() + 1);
            teamLoser.setLosses(teamLoser.getLosses() + 1);

            teamWinner.setPointsFor(teamWinner.getPointsFor() + resultTeamWinner.getPoints());
            teamWinner.setPointsAgainst(teamWinner.getPointsAgainst() + resultTeamLoser.getPoints());

            teamLoser.setPointsFor(teamLoser.getPointsFor() + resultTeamLoser.getPoints());
            teamLoser.setPointsAgainst(teamLoser.getPointsAgainst() + resultTeamWinner.getPoints());
        }
    }

    private void verifyBonusPoints(TeamStanding teamWinner, TeamStanding teamLoser,
                                   TeamResultsDto resultTeamWinner, TeamResultsDto resultTeamLoser,
                                   boolean isDraw) {
        verifyTriesForBonusPoint(teamWinner, resultTeamWinner);
        verifyTriesForBonusPoint(teamLoser, resultTeamLoser);

        if (resultTeamWinner.getPoints() - 7 <= resultTeamLoser.getPoints() && !isDraw) {
            teamLoser.setBonusPoints(teamLoser.getBonusPoints() + 1);
        }
    }

    private void verifyTriesForBonusPoint(TeamStanding team, TeamResultsDto teamResult) {
        team.setTriesMade(team.getTriesMade() + teamResult.getTries());
        if (teamResult.getTries() >= 4) {
            team.setBonusPoints(team.getBonusPoints() + 1);
        }
    }


    private TeamStanding getTeamsOfResult(PoolResultsDto poolResultsDto, Long teamId) {
        for (TeamStanding teamStanding : poolResultsDto.getTeams()) {
            if (Objects.equals(teamStanding.getTeamId(), teamId)) {
                return teamStanding;
            }
        }
        throw new EntityNotFoundException("No existe el equipo: ");
    }
}
