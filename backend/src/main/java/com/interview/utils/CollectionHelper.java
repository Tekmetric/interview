package com.interview.utils;

import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CollectionHelper {

  public static <I, O> Set<O> convertSet(Set<I> set, Function<I, O> conversion) {
    if (set == null) {
      return Collections.emptySet();
    }
    return set.stream()
        .map(conversion)
        .collect(Collectors.toSet());
  }
}
