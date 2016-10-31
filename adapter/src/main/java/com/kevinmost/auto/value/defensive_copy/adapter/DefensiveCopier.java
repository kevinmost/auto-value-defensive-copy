package com.kevinmost.auto.value.defensive_copy.adapter;

import org.jetbrains.annotations.NotNull;

/**
 * A functional interface that defines how to return a defensive copy of a value of the given type.
 */
public interface DefensiveCopier<T> {
  /**
   * A method that returns a defensive copy of the given input.
   *
   * @param source the original object that must be defensively copied. This will never be {@code null}
   * @return a copy of the original object that will not mutate the original object if it is mutated itself
   */
  @NotNull T defensiveCopy(@NotNull T source);
}
