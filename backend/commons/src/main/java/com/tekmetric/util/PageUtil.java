package com.tekmetric.util;

import java.util.List;
import java.util.function.Function;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@UtilityClass
public class PageUtil {
  public static <T, R> Page<R> mapList(Page<T> page, Function<List<T>, List<R>> listMapper) {
    return new PageImpl<>(
        listMapper.apply(page.getContent()), page.getPageable(), page.getTotalElements());
  }
}
