package com.mika.ktdcloud.community.controller;

import com.mika.ktdcloud.community.dto.term.TermDTO;
import com.mika.ktdcloud.community.service.TermService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/terms")
@RequiredArgsConstructor
public class TermController {
    private final TermService termService;

    @GetMapping
    public ResponseEntity<List<TermDTO>> getAllTerms() {
        return ResponseEntity.ok(termService.findAllTerms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TermDTO> getTermDetail(@PathVariable Long id) {
        return ResponseEntity.ok(termService.findTermById(id));
    }
}
