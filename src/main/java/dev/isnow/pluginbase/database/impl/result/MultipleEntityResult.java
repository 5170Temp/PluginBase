package dev.isnow.pluginbase.database.impl.result;

import dev.isnow.pluginbase.util.ExpiringSession;
import lombok.Value;

import java.util.List;

@Value
public class MultipleEntityResult<T> {
    ExpiringSession session;

    List<T> entity;
}