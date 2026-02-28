package com.pashkevich.dmonitorapp.controller;

import com.pashkevich.dmonitorapp.model.DatabaseConnectionConfig;
import com.pashkevich.dmonitorapp.repository.DatabaseConnectionRepository;
import com.pashkevich.dmonitorapp.service.MonitoringService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static reactor.core.publisher.Mono.when;

@ExtendWith(MockitoExtension.class)
public class DatabaseControllerTest {

    @Mock
    private DatabaseConnectionRepository databaseConnectionRepository;

    @Mock
    private MonitoringService monitoringService;

    @InjectMocks
    private DatabaseHealthController databaseHealthController;

    @Test
    void getAllConnections_ShouldReturnAllConnections() {
        // Arrange
        DatabaseConnectionConfig config1 = DatabaseConnectionConfig.builder()
                .id(1L)
                .name("DB1")
                .connectionUrl("r2dbc:postgresql://localhost:5432/db1")
                .build();

        DatabaseConnectionConfig config2 = DatabaseConnectionConfig.builder()
                .id(2L)
                .name("DB2")
                .connectionUrl("r2dbc:postgresql://localhost:5432/db2")
                .build();

        when(databaseConnectionRepository.findAll())
                .thenReturn(Flux.just(config1, config2));

        // Act & Assert
        Flux<DatabaseConnectionConfig> result = databaseHealthController.getAllConnections();

        StepVerifier.create(result)
                .expectNext(config1)
                .expectNext(config2)
                .verifyComplete();
    }

    @Test
    void getConnectionById_ShouldReturnConnection_WhenExists() {
        // Arrange
        DatabaseConnectionConfig config = DatabaseConnectionConfig.builder()
                .id(1L)
                .name("DB1")
                .connectionUrl("r2dbc:postgresql://localhost:5432/db1")
                .build();

        when(databaseConnectionRepository.findById(1L))
                .thenReturn(Mono.just(config));

        // Act & Assert
        Mono<DatabaseConnectionConfig> result = databaseHealthController.getConnectionById(1L);

        StepVerifier.create(result)
                .expectNext(config)
                .verifyComplete();
    }

    @Test
    void createConnection_ShouldSaveAndReturnConnection() {
        // Arrange
        DatabaseConnectionConfig config = DatabaseConnectionConfig.builder()
                .name("New DB")
                .connectionUrl("r2sbc:postgresql://localhost:5432/newdb")
                .username("ser")
                .password("pass")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        DatabaseConnectionConfig saveConfig = DatabaseConnectionConfig.builder()
                .id(1L)
                .name("New DB")
                .connectionUrl("r2dbc:postgresql:localhost:5432/newdb")
                .username("user")
                .password("pass")
                .isActive(true)
                .createdAt(config.getCreatedAt())
                .build();

        when(databaseConnectionRepository.save(any(DatabaseConnectionConfig.class)))
                .thenReturn(Mono.just(saveConfig));

        // Act & Assert
        Mono<DatabaseConnectionConfig> result = databaseHealthController.createConnection(config);

        StepVerifier.create(result)
                .expectNext(saveConfig)
                .verifyComplete();
    }

    @Test
    void deleteConnection_ShouldCallRepositoryDelete() {
        // Arrange
        when(databaseConnectionRepository.deleteById(1L))
                .thenReturn(Mono.empty());

        // Act
        Mono<Void> result = databaseHealthController.deleteConnection(1L);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();
    }
}
