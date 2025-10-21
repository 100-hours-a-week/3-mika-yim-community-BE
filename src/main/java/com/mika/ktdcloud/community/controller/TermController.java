package com.mika.ktdcloud.community.controller;

import com.mika.ktdcloud.community.dto.term.TermDTO;
import com.mika.ktdcloud.community.service.TermService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/terms")
@RequiredArgsConstructor
public class TermController {
    private final TermService termService;

    @GetMapping
    public String showTermsPage(Model model) {
        // 약관 목록 불러오기
        List<TermDTO> terms = termService.findAllTerms();
        model.addAttribute("terms", terms);
        return "terms"; // terms.html 파일을 랜더링
    }

    @GetMapping("/{id}")
    public String showTermDetailPage(@PathVariable Long id, Model model) {
        TermDTO term = termService.findTermById(id);
        model.addAttribute("term", term);
        return "term-detail"; // "term-detail.html" 템플릿을 렌더링
    }
}
