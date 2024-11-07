package ar.edu.utn.frc.tup.lciii.controllers;

import ar.edu.utn.frc.tup.lciii.dtos.common.PoolDto;
import ar.edu.utn.frc.tup.lciii.dtos.common.PoolResultsDto;
import ar.edu.utn.frc.tup.lciii.models.Team;
import ar.edu.utn.frc.tup.lciii.models.TeamStanding;
import ar.edu.utn.frc.tup.lciii.services.impl.PoolServiceImpl;
import org.apache.tomcat.jni.Pool;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PoolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PoolServiceImpl poolService;

    @Test
    void getAllPoolsTest() throws Exception {
        List<PoolDto> listPoolDto = new ArrayList<>();
        PoolDto poolDto = new PoolDto();
        poolDto.setPool("A");
        poolDto.setTeams(new ArrayList<>());
        poolDto.getTeams().add(new Team(1L, "Team 1", "Arg", 1, "A"));
        listPoolDto.add(poolDto);


        when(poolService.getAllPools()).thenReturn(listPoolDto);
        this.mockMvc.perform(get("/rwc/2023/pools"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pool").value("A"))
                .andExpect(jsonPath("$[0].teams[0].id").value(1L));
    }

    @Test
    void getPoolByIdTest() throws Exception {
        PoolDto poolDto = new PoolDto();
        poolDto.setPool("A");
        poolDto.setTeams(new ArrayList<>());
        poolDto.getTeams().add(new Team(1L, "Team 1", "Arg", 1, "A"));

        when(poolService.getPool("A")).thenReturn(poolDto);

        this.mockMvc.perform(get("/rwc/2023/pool/A"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pool").value("A"))
                .andExpect(jsonPath("$.teams[0].id").value(1L));
    }

    @Test
    void getResultPoolTest() throws Exception {
        PoolResultsDto poolResultsDto = new PoolResultsDto();
        poolResultsDto.setPoolId("A");
        poolResultsDto.setTeams(new ArrayList<>());

        TeamStanding team1 = new TeamStanding();
        team1.setTeamId(1L);
        TeamStanding team2 = new TeamStanding();
        team2.setTeamId(2L);

        poolResultsDto.getTeams().add(team1);
        poolResultsDto.getTeams().add(team2);

        when(poolService.calculatePoints("A")).thenReturn(poolResultsDto);

        this.mockMvc.perform(get("/rwc/2023/results/A"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teams", hasSize(2)))
                .andExpect(jsonPath("$.teams[0].teamId").value(1L))
                .andExpect(jsonPath("$.teams[1].teamId").value(2L));
    }
}
