package com.sngular.core.resilience.eventlog;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.event.TimeLimiterEvent;
import lombok.extern.slf4j.Slf4j;

@Component
@ConditionalOnProperty(name = "resilience4j.timelimiter.log.enable", havingValue = "true")
@Slf4j
public class TimeLimiterEventLogger extends AbstractResilence4jEventLogger<TimeLimiter> {

  @Value("${resilience4j.timelimiter.log.excludes:}")
  private List<TimeLimiterEvent.Type> excludeTypes;

  @Override
  protected Logger getLogger() {
    return log;
  }

  @Override
  protected void writeDebug(final TimeLimiter entry) {
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
