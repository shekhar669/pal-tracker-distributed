package io.pivotal.pal.tracker.allocations;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.web.client.RestOperations;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class ProjectClient {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final RestOperations restOperations;
    private final String registrationServerEndpoint;
    private Map<Long,ProjectInfo> cacheProjInfo= new ConcurrentHashMap<>();

    public ProjectClient(RestOperations restOperations, String registrationServerEndpoint) {
        this.restOperations= restOperations;
        this.registrationServerEndpoint = registrationServerEndpoint;
    }
    @CircuitBreaker(name = "project", fallbackMethod = "getProjectFromCache")
    public ProjectInfo getProject(long projectId) {
        ProjectInfo projInfo= restOperations.getForObject(registrationServerEndpoint + "/projects/" + projectId, ProjectInfo.class);
        cacheProjInfo.put(projectId,projInfo);
        return projInfo;
    }

    public ProjectInfo getProjectFromCache(long projectId, Throwable cause) {
        logger.info("Getting project with id {} from cache", projectId);
        return cacheProjInfo.get(projectId);
    }
}

