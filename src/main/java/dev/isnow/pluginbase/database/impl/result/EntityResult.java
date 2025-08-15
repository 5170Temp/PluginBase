package dev.isnow.pluginbase.database.impl.result;

import dev.isnow.pluginbase.util.ExpiringSession;
import lombok.Value;

@Value
public class EntityResult<T> {
    ExpiringSession session;

    T entity;
}