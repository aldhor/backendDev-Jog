package com.sngular.core.resilience.eventlog;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.event.RetryEvent;
import lombok.extern.slf4j.Slf4j;

@Component
@ConditionalOnProperty(name = "resilience4j.retry.log.enable", havingValue = "true")
@Slf4j
public class RetryEventLogger extends AbstractResilence4jEventLogger<Retry> {

  @Value("${resilience4j.retry.log.excludes:}")
  private List<RetryEvent.Type> excludeTypes;

  @Override
  protected Logger getLogger() {
    return log;
  }

  @Override
  protected void writeDebug(final Retry entry) {
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
