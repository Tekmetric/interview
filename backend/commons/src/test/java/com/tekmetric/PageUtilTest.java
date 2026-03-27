package com.tekmetric;

import static org.junit.jupiter.api.Assertions.*;

import com.tekmetric.util.PageUtil;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;

class PageUtilTest {

  @Test
  void mapList_mapsContentAndRetainsPageMetadata() {
    Pageable pageable = PageRequest.of(2, 5); // page index = 2
    List<Integer> content = List.of(1, 2, 3);
    Page<Integer> page = new PageImpl<>(content, pageable, 30);

    Page<String> mapped =
        PageUtil.mapList(page, ints -> ints.stream().map(Object::toString).toList());

    assertEquals(page.getNumber(), mapped.getNumber());
    assertEquals(page.getSize(), mapped.getSize());
    assertEquals(page.getTotalElements(), mapped.getTotalElements());
    assertEquals(List.of("1", "2", "3"), mapped.getContent());
  }
}
