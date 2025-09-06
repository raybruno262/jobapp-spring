package com.MidtermProject.service;
import com.MidtermProject.repository.JobListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.MidtermProject.repository.ApplicationRepository;
@Service
@RequiredArgsConstructor
public class GlobalsearchService {
    private final ApplicationRepository applicationRepository;
    private final JobListingRepository jobListingRepository;
    public Map<String, List<?>> search(String q) {
        Map<String, List<?>> p = new HashMap<>();
        if ( q.trim().isEmpty() || q == null) {
            p.put("application", List.of());
            p.put("joblisting", List.of());   
            return p;
        }
        p.put("application", applicationRepository.search(q));
        p.put("joblisting", jobListingRepository.search(q));   
        return p;
    }
}
