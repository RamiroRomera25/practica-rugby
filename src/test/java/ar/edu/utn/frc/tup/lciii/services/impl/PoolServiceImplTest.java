package ar.edu.utn.frc.tup.lciii.services.impl;

import ar.edu.utn.frc.tup.lciii.client.MatchDto;
import ar.edu.utn.frc.tup.lciii.client.MatchRestClient;
import ar.edu.utn.frc.tup.lciii.client.TeamDto;
import ar.edu.utn.frc.tup.lciii.client.TeamRestClient;
import ar.edu.utn.frc.tup.lciii.client.TeamResultsDto;
import ar.edu.utn.frc.tup.lciii.dtos.common.PoolDto;
import ar.edu.utn.frc.tup.lciii.dtos.common.PoolResultsDto;
import ar.edu.utn.frc.tup.lciii.models.TeamStanding;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PoolServiceImplTest {
    @Mock
    private TeamRestClient teamRestClient;

    @Mock
    private MatchRestClient matchRestClient;

    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private PoolServiceImpl poolService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllPoolsTest() {
        TeamDto team1 = new TeamDto(1L, "Team 1", "Argentina", 1, "A");
        TeamDto team2 = new TeamDto(2L, "Team 2", "Argentina", 2, "B");
        TeamDto team3 = new TeamDto(3L, "Team 3", "Argentina", 3, "C");
        TeamDto team4 = new TeamDto(4L, "Team 4", "Argentina", 4, "D");

        when(teamRestClient.getTeams())
                .thenReturn(ResponseEntity.ok(new TeamDto[]{team1, team2, team3, team4}));


        List<PoolDto> result = poolService.getAllPools();

        assertEquals(1L, result.get(0).getTeams().get(0).getId());
        assertEquals(2L, result.get(1).getTeams().get(0).getId());
        assertEquals(3L, result.get(2).getTeams().get(0).getId());
        assertEquals(4L, result.get(3).getTeams().get(0).getId());
    }

    @Test
    void getPoolTest() {
        TeamDto team1 = new TeamDto(1L, "Team 1", "Argentina", 1, "A");
        TeamDto team2 = new TeamDto(2L, "Team 2", "Argentina", 2, "B");
        TeamDto team3 = new TeamDto(3L, "Team 3", "Argentina", 3, "C");
        TeamDto team4 = new TeamDto(4L, "Team 4", "Argentina", 4, "D");

        when(teamRestClient.getTeams())
                .thenReturn(ResponseEntity.ok(new TeamDto[]{team1, team2, team3, team4}));


        PoolDto result = poolService.getPool("A");

        assertEquals(1L, result.getTeams().get(0).getId());
        assertEquals(1, result.getTeams().size());
    }

    @Test
    void calculateMiscellaneous() {
        TeamStanding teamStanding = new TeamStanding();
        teamStanding.setPointsFor(100);
        teamStanding.setPointsAgainst(25);
        teamStanding.setTotalYellowCards(100);
        teamStanding.setTotalRedCards(100);
        teamStanding.setMatchesPlayed(100);
        teamStanding.setWins(10);
        teamStanding.setDraws(5);
        teamStanding.setBonusPoints(5);

        TeamResultsDto teamResultsDto = new TeamResultsDto();
        teamResultsDto.setYellowCards(50);
        teamResultsDto.setRedCards(50);

        ReflectionTestUtils.invokeMethod(poolService, "calculateMiscellaneous", teamStanding, teamResultsDto);

        assertEquals(50, teamStanding.getPoints());
        assertEquals(150, teamStanding.getTotalYellowCards());
        assertEquals(150, teamStanding.getTotalRedCards());
        assertEquals(75, teamStanding.getPointsDifferential());
        assertEquals(101, teamStanding.getMatchesPlayed());
    }

    @Test
    void calculateStandingsTest_IsDrawFalse() {
        TeamStanding teamStandingWinner = new TeamStanding();
        teamStandingWinner.setPointsFor(100);
        teamStandingWinner.setPointsAgainst(25);
        teamStandingWinner.setWins(10);

        TeamResultsDto teamResultWinner = new TeamResultsDto();
        teamResultWinner.setPoints(30);


        TeamStanding teamStandingLoser = new TeamStanding();
        teamStandingLoser.setPointsFor(100);
        teamStandingLoser.setPointsAgainst(25);
        teamStandingLoser.setLosses(10);

        TeamResultsDto teamResultLoser = new TeamResultsDto();
        teamResultLoser.setPoints(10);


        ReflectionTestUtils.invokeMethod(poolService, "calculateStandings", teamStandingWinner, teamStandingLoser,
                teamResultWinner, teamResultLoser, false);


        assertEquals(11, teamStandingWinner.getWins());
        assertEquals(130, teamStandingWinner.getPointsFor());
        assertEquals(35, teamStandingWinner.getPointsAgainst());

        assertEquals(11, teamStandingLoser.getLosses());
        assertEquals(55, teamStandingLoser.getPointsAgainst());
        assertEquals(110, teamStandingLoser.getPointsFor());
    }

    @Test
    void calculateStandingsTest_IsDrawTrue() {
        TeamStanding teamStandingWinner = new TeamStanding();
        teamStandingWinner.setPointsFor(100);
        teamStandingWinner.setPointsAgainst(25);
        teamStandingWinner.setWins(10);
        teamStandingWinner.setDraws(1);

        TeamResultsDto teamResultWinner = new TeamResultsDto();
        teamResultWinner.setPoints(10);


        TeamStanding teamStandingLoser = new TeamStanding();
        teamStandingLoser.setPointsFor(100);
        teamStandingLoser.setPointsAgainst(25);
        teamStandingLoser.setLosses(10);
        teamStandingLoser.setDraws(1);

        TeamResultsDto teamResultLoser = new TeamResultsDto();
        teamResultLoser.setPoints(10);


        ReflectionTestUtils.invokeMethod(poolService, "calculateStandings", teamStandingWinner, teamStandingLoser,
                teamResultWinner, teamResultLoser, true);


        assertEquals(10, teamStandingWinner.getWins());
        assertEquals(110, teamStandingWinner.getPointsFor());
        assertEquals(35, teamStandingWinner.getPointsAgainst());
        assertEquals(2, teamStandingWinner.getDraws());

        assertEquals(10, teamStandingLoser.getLosses());
        assertEquals(35, teamStandingLoser.getPointsAgainst());
        assertEquals(110, teamStandingLoser.getPointsFor());
        assertEquals(2, teamStandingLoser.getDraws());
    }

    @Test
    void verifyBonusPointsTest_NotDraw() {
        TeamStanding teamStandingWinner = new TeamStanding();
        teamStandingWinner.setBonusPoints(0);
        teamStandingWinner.setTriesMade(0);

        TeamResultsDto teamResultWinner = new TeamResultsDto();
        teamResultWinner.setPoints(30);
        teamResultWinner.setTries(5);


        TeamStanding teamStandingLoser = new TeamStanding();
        teamStandingLoser.setBonusPoints(0);
        teamStandingLoser.setTriesMade(0);

        TeamResultsDto teamResultLoser = new TeamResultsDto();
        teamResultLoser.setPoints(25);
        teamResultLoser.setTries(2);


        ReflectionTestUtils.invokeMethod(poolService, "verifyBonusPoints", teamStandingWinner, teamStandingLoser,
                teamResultWinner, teamResultLoser, false);

        assertEquals(1, teamStandingWinner.getBonusPoints());
        assertEquals(1, teamStandingLoser.getBonusPoints());
    }

    @Test
    void verifyBonusPointsTest_Draw() {
        TeamStanding teamStandingWinner = new TeamStanding();
        teamStandingWinner.setBonusPoints(0);
        teamStandingWinner.setTriesMade(0);

        TeamResultsDto teamResultWinner = new TeamResultsDto();
        teamResultWinner.setPoints(30);
        teamResultWinner.setTries(3);


        TeamStanding teamStandingLoser = new TeamStanding();
        teamStandingLoser.setBonusPoints(0);
        teamStandingLoser.setTriesMade(0);

        TeamResultsDto teamResultLoser = new TeamResultsDto();
        teamResultLoser.setPoints(25);
        teamResultLoser.setTries(2);


        ReflectionTestUtils.invokeMethod(poolService, "verifyBonusPoints", teamStandingWinner, teamStandingLoser,
                teamResultWinner, teamResultLoser, true);

        assertEquals(0, teamStandingWinner.getBonusPoints());
        assertEquals(0, teamStandingLoser.getBonusPoints());
    }

    @Test
    void getTeamsOfResultTest_Succesful() {
        TeamStanding team1 = new TeamStanding();
        team1.setTeamId(1L);
        TeamStanding team2 = new TeamStanding();
        team2.setTeamId(2L);

        PoolResultsDto poolResultsDto = new PoolResultsDto();
        poolResultsDto.setTeams(new ArrayList<>());
        poolResultsDto.getTeams().add(team1);
        poolResultsDto.getTeams().add(team2);

        TeamStanding result = ReflectionTestUtils.invokeMethod(poolService, "getTeamsOfResult", poolResultsDto, 1L);

        assertEquals(1L, result.getTeamId());
    }

    @Test
    void getTeamsOfResultTest_Failure() {
        PoolResultsDto poolResultsDto = new PoolResultsDto();
        poolResultsDto.setTeams(new ArrayList<>());

        assertThrows(EntityNotFoundException.class, () -> {
            ReflectionTestUtils.invokeMethod(poolService, "getTeamsOfResult", poolResultsDto, 3L);
        });
    }

    @Test
    void fallbackTest() {
        assertThrows(HttpClientErrorException.class, () -> {
            ReflectionTestUtils.invokeMethod(poolService, "fallback", new Exception());
        });
    }

    @Test
    void getAllMatchesTest() {
        MatchDto match1 = new MatchDto();
        match1.setId(1L);
        match1.setPool("A");
        MatchDto match2 = new MatchDto();
        match2.setId(2L);
        match2.setPool("A");

        when(matchRestClient.getAllMatches())
                .thenReturn(ResponseEntity.ok(new MatchDto[]{match1, match2}));

        List<MatchDto> result = ReflectionTestUtils.invokeMethod(poolService, "getAllMatches", "A");

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }
}
