package com.zimmerh0.medicalclinic.controller;

import com.zimmerh0.medicalclinic.dto.visit.BookVisitDto;
import com.zimmerh0.medicalclinic.dto.visit.CreateVisitDto;
import com.zimmerh0.medicalclinic.dto.visit.VisitDto;
import com.zimmerh0.medicalclinic.dto.visit.VisitMapper;
import com.zimmerh0.medicalclinic.service.VisitService;
import com.zimmerh0.medicalclinic.visit.Visit;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/visits")
@RequiredArgsConstructor
public class VisitController {

    private final VisitService visitService;
    private final VisitMapper visitMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VisitDto createVisit(@Valid @RequestBody CreateVisitDto dto) {
        Visit visit = visitService.addVisit(dto);
        return visitMapper.toDto(visit);
    }

    @PutMapping("/{visitId}/book")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void bookVisit(@PathVariable Long visitId, @Valid @RequestBody BookVisitDto dto) {
        visitService.bookVisit(visitId, dto);
    }
}
