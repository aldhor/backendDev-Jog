package com.sngular.core.resilience.eventlog;

import org.slf4j.Logger;

import io.github.resilience4j.core.registry.EntryAddedEvent;
import io.github.resilience4j.core.registry.EntryRemovedEvent;
import io.github.resilience4j.core.registry.EntryReplacedEvent;
import io.github.resilience4j.core.registry.RegistryEventConsumer;

public abstract class AbstractResilence4jEventLogger<T> implements RegistryEventConsumer<T> {

  private static final String LOG_PATTERN = "[{}] - {}";

  @Override
  public void onEntryAddedEvent(final EntryAddedEvent<T> entryAddedEvent) {
    if (getLogger().isDebugEnabled()) {
      writeDebug(entryAddedEvent.getAddedEntry());
    }
  }

  @Override
  public void onEntryRemovedEvent(final EntryRemovedEvent<T> entryRemoveEvent) {
    //
  }

  @Override
  public void onEntryReplacedEvent(final EntryReplacedEvent<T> entryReplacedEvent) {
    //
  }

  protected void writeToLog(final String eventType, final String eventString) {
    getLogger().debug(LOG_PATTERN, eventType, eventString);
  }

  protected abstract Logger getLogger();

  protected abstract void writeDebug(T entry);
}
