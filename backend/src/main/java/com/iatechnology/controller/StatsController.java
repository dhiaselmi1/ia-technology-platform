package com.iatechnology.controller;

import com.iatechnology.model.Domain;
import com.iatechnology.model.Publication;
import com.iatechnology.model.Role;
import com.iatechnology.model.User;
import com.iatechnology.repository.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stats")
@PreAuthorize("hasRole('ADMIN')")
public class StatsController {

    private final ResearcherRepository researcherRepo;
    private final PublicationRepository publicationRepo;
    private final DomainRepository domainRepo;
    private final UserRepository userRepo;
    private final NewsRepository newsRepo;
    private final AuditLogRepository auditLogRepo;

    public StatsController(ResearcherRepository researcherRepo, PublicationRepository publicationRepo,
                           DomainRepository domainRepo, UserRepository userRepo,
                           NewsRepository newsRepo, AuditLogRepository auditLogRepo) {
        this.researcherRepo = researcherRepo;
        this.publicationRepo = publicationRepo;
        this.domainRepo = domainRepo;
        this.userRepo = userRepo;
        this.newsRepo = newsRepo;
        this.auditLogRepo = auditLogRepo;
    }

    @GetMapping("/overview")
    public Map<String, Object> overview() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("researchers", researcherRepo.count());
        stats.put("publications", publicationRepo.count());
        stats.put("domains", domainRepo.count());
        stats.put("users", userRepo.count());
        stats.put("news", newsRepo.count());
        return stats;
    }

    @GetMapping("/recent-activity")
    public Object recentActivity() {
        return auditLogRepo.findAll(org.springframework.data.domain.PageRequest.of(0, 8,
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"))).getContent();
    }

    @GetMapping("/publications-by-domain")
    public List<Map<String, Object>> publicationsByDomain() {
        List<Domain> domains = domainRepo.findAll();
        List<Publication> allPubs = publicationRepo.findAll();

        Map<Long, Long> countByDomain = allPubs.stream()
                .filter(p -> p.getDomain() != null)
                .collect(Collectors.groupingBy(p -> p.getDomain().getId(), Collectors.counting()));

        return domains.stream().map(d -> {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("domain", d.getName());
            entry.put("count", countByDomain.getOrDefault(d.getId(), 0L));
            return entry;
        }).sorted((a, b) -> Long.compare((long) b.get("count"), (long) a.get("count")))
          .collect(Collectors.toList());
    }

    @GetMapping("/publications-by-month")
    public List<Map<String, Object>> publicationsByMonth() {
        List<Publication> pubs = publicationRepo.findAll();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");

        YearMonth now = YearMonth.now();
        List<YearMonth> months = new ArrayList<>();
        for (int i = 11; i >= 0; i--) {
            months.add(now.minusMonths(i));
        }

        Map<String, Long> countMap = pubs.stream()
                .filter(p -> p.getPublishedDate() != null || p.getCreatedAt() != null)
                .collect(Collectors.groupingBy(
                        p -> {
                            LocalDateTime date = p.getPublishedDate() != null ? p.getPublishedDate() : p.getCreatedAt();
                            return YearMonth.from(date).format(fmt);
                        },
                        Collectors.counting()));

        return months.stream().map(ym -> {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("month", ym.format(fmt));
            entry.put("count", countMap.getOrDefault(ym.format(fmt), 0L));
            return entry;
        }).collect(Collectors.toList());
    }

    @GetMapping("/users-by-role")
    public List<Map<String, Object>> usersByRole() {
        List<User> users = userRepo.findAll();
        Map<Role, Long> countMap = users.stream()
                .collect(Collectors.groupingBy(User::getRole, Collectors.counting()));

        return Arrays.stream(Role.values()).map(role -> {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("role", role.name());
            entry.put("count", countMap.getOrDefault(role, 0L));
            return entry;
        }).collect(Collectors.toList());
    }
}
