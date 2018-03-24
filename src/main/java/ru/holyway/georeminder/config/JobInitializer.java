package ru.holyway.georeminder.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

@Component
public class JobInitializer {

    private final static Logger LOGGER = LoggerFactory.getLogger(JobInitializer.class);
    private static final long DELAY_TO_UPDATE = TimeUnit.MINUTES.toMillis(15);

    private final TaskScheduler scheduler;
    private final String selfUrl;
    private final RestTemplate restTemplate;

    public JobInitializer(@Value("${bot.url}") String selfUrl,
                          RestTemplate restTemplate) {
        this.scheduler = new ConcurrentTaskScheduler();
        this.selfUrl = selfUrl;
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    private void job() {
        scheduler.scheduleWithFixedDelay(() -> {
            try {
                restTemplate.getForObject(new URI(selfUrl), String.class);
            } catch (URISyntaxException e) {
                LOGGER.error("Error during bot ping!", e);
            }
        }, DELAY_TO_UPDATE);
    }
}
