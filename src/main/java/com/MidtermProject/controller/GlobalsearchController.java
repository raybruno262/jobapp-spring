package com.MidtermProject.controller;
import com.MidtermProject.service.GlobalsearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/globalSearch")
public class GlobalsearchController {
    @Autowired
    private GlobalsearchService globalSearchService;
    @GetMapping
    public Map<String, List<?>> search(@RequestParam String q) {
        return globalSearchService.search(q);
    }
}