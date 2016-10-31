package com.kevinmost.auto.value.defensive_copy.adapter;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Apply this annotation to an AutoValue property to indicate that the
 * generated getter should not return the underlying object, but a defensive copy of it.
 * <p>
 * For common types, the logic to make this defensive copy is provided. For example:
 * <p>
 * {@link List} -> {@link Collections#unmodifiableList(List)}
 * {@link Set} -> {@link Collections#unmodifiableSet(Set)}
 * {@link Map} -> {@link Collections#unmodifiableMap(Map)}
 * {@link Date} -> {@link Date#Date(long)} (using the existing Date's timestamp)
 */
public @interface DefensiveCopy {
  @NotNull Class<? extends DefensiveCopier> copier() default DefensiveCopier.class;
}
