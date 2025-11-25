package com.interview.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

	@Test
	void testToString() {
		final Money money = new Money(123);
		final String result = money.toString();
		assertEquals("$1.23", result);
	}
}