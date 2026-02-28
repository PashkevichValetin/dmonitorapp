package com.pashkevich.dmonitorapp.config;

import com.pashkevich.dmonitorapp.service.MonitoringService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class MonitoringSchedulerIntegrationTest {

    @SpyBean
    private MonitoringService monitoringService;

    @Autowired
    private MonitoringScheduler monitoringScheduler;

    @Test
    void runMonitoring_ShouldCallPerformChecks() {
        // Arrange
        CompletableFuture<String> future = CompletableFuture.completedFuture("Success");

        // Act
        monitoringScheduler.runMonitoring();

        // Assert
        await()
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    verify(monitoringService, atLeast(1)).performChecks();
                });
    }
}
