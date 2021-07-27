package com.sngular.core.resilience.eventlog;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.event.BulkheadEvent;
import lombok.extern.slf4j.Slf4j;

@Component
@ConditionalOnProperty(name = "resilience4j.bulkhead.log.enable", havingValue = "true")
@Slf4j
public class BulkheadEventLogger extends AbstractResilence4jEventLogger<Bulkhead> {

  @Value("${resilience4j.bulkhead.log.excludes:}")
  private List<BulkheadEvent.Type> excludeTypes;

  @Override
  protected Logger getLogger() {
    return log;
  }

  @Override
  protected void writeDebug(final Bulkhead entry) {
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
