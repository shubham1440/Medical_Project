package com.healthcare.scheduler;


import com.healthcare.models.enums.ConsentStatus;
import com.healthcare.repo.ConsentRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class ConsentScheduler {
    private final ConsentRepository repository;

    @Scheduled(fixedRate = 60000)
    public void expireConsents() {
        // Example: Expire consents granted more than 24 hours ago
        LocalDateTime threshold = LocalDateTime.now().minusHours(24);

        var expired = repository.findAllByConsentStatusAndGrantedAtBefore(
                ConsentStatus.APPROVED, threshold);

        if (!expired.isEmpty()) {
            expired.forEach(c -> {
                c.setConsentStatus(ConsentStatus.EXPIRED);
                c.setIsActive(false);
            });
            repository.saveAll(expired);
        }
    }
}