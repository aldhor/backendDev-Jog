package com.sngular.core.resilience.eventlog;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerEvent;
import lombok.extern.slf4j.Slf4j;

@Component
@ConditionalOnProperty(name = "resilience4j.circuitbreaker.log.enable", havingValue = "true")
@Slf4j
public class CircuitBreakerEventLogger extends AbstractResilence4jEventLogger<CircuitBreaker> {

  @Value("${resilience4j.circuitbreaker.log.excludes:}")
  private List<CircuitBreakerEvent.Type> excludeTypes;

  @Override
  protected Logger getLogger() {
    return log;
  }

  @Override
  protected void writeDebug(final CircuitBreaker entry) {
    entry
        .getEventPublisher()
        .onEvent(
            event -> {
              if (!excludeTypes.contains(event.getEventType())) {
                writeToLog(event.getEventType().name(), event.toString());
              }
            });
  }
}
