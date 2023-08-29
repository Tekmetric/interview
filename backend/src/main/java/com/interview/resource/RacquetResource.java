package com.interview.resource;

import com.interview.dto.RacquetDto;
import com.interview.service.RacquetService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/racquets")
public class RacquetResource {
    private final RacquetService racquetService;

    public RacquetResource(RacquetService racquetService) {
        this.racquetService = racquetService;
    }

    @GetMapping()
    public List<RacquetDto> getRacquests() {
        return racquetService.findAll();
    }

}
