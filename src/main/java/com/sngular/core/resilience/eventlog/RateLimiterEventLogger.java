package com.sngular.core.resilience.eventlog;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.event.RateLimiterEvent;
import lombok.extern.slf4j.Slf4j;

@Component
@ConditionalOnProperty(name = "resilience4j.ratelimiter.log.enable", havingValue = "true")
@Slf4j
public class RateLimiterEventLogger extends AbstractResilence4jEventLogger<RateLimiter> {

  @Value("${resilience4j.ratelimiter.log.excludes:}")
  private List<RateLimiterEvent.Type> excludeTypes;

  @Override
  protected Logger getLogger() {
    return log;
  }

  @Override
  protected void writeDebug(final RateLimiter entry) {
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
