package ar.edu.utn.frc.tup.lciii.controllers;

import ar.edu.utn.frc.tup.lciii.dtos.common.ErrorApi;
import ar.edu.utn.frc.tup.lciii.dtos.common.PoolDto;
import ar.edu.utn.frc.tup.lciii.dtos.common.PoolResultsDto;
import ar.edu.utn.frc.tup.lciii.services.PoolService;
import ar.edu.utn.frc.tup.lciii.services.impl.PoolServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rwc/2023")
public class PoolController {

    @Autowired
    private PoolServiceImpl poolService;

    @GetMapping("/pools")
    public ResponseEntity<List<PoolDto>> getAllPools() {
        return ResponseEntity.ok(poolService.getAllPools());
    }

    @GetMapping("/pool/{group}")
    public ResponseEntity<PoolDto> getPoolById(@PathVariable String group) {
        return ResponseEntity.ok(poolService.getPool(group));
    }

    @GetMapping("/results/{group}")
    public ResponseEntity<PoolResultsDto> getResultPool(@PathVariable String group) {
        return ResponseEntity.ok(poolService.calculatePoints(group));
    }
}
