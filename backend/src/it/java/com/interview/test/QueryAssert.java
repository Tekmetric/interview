package com.interview.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.EnumSet;
import java.util.Set;
import org.assertj.core.api.AbstractAssert;
import org.hibernate.stat.Statistics;

public final class QueryAssert extends AbstractAssert<QueryAssert, Statistics> {

    private enum StatType {
        QUERY, INSERT, UPDATE, DELETE, COLLECTION_FETCH
    }

    private final Set<StatType> asserted = EnumSet.noneOf(StatType.class);

    private QueryAssert(Statistics statistics) {
        super(statistics, QueryAssert.class);
    }

    public static QueryAssert assertThatQuery(Statistics statistics) {
        return new QueryAssert(statistics);
    }

    public QueryAssert hasQueryCount(long expected) {
        isNotNull();
        asserted.add(StatType.QUERY);
        assertThat(actual.getQueryExecutionCount()).as("query execution count").isEqualTo(expected);
        return myself;
    }

    public QueryAssert hasInsertCount(long expected) {
        isNotNull();
        asserted.add(StatType.INSERT);
        assertThat(actual.getEntityInsertCount()).as("entity insert count").isEqualTo(expected);
        return myself;
    }

    public QueryAssert hasUpdateCount(long expected) {
        isNotNull();
        asserted.add(StatType.UPDATE);
        assertThat(actual.getEntityUpdateCount()).as("entity update count").isEqualTo(expected);
        return myself;
    }

    public QueryAssert hasDeleteCount(long expected) {
        isNotNull();
        asserted.add(StatType.DELETE);
        assertThat(actual.getEntityDeleteCount()).as("entity delete count").isEqualTo(expected);
        return myself;
    }

    public QueryAssert hasCollectionFetchCount(long expected) {
        isNotNull();
        asserted.add(StatType.COLLECTION_FETCH);
        assertThat(actual.getCollectionFetchCount()).as("collection fetch count").isEqualTo(expected);
        return myself;
    }

    public QueryAssert hasNoOtherOperations() {
        isNotNull();
        if (!asserted.contains(StatType.QUERY)) {
            assertThat(actual.getQueryExecutionCount()).as("unexpected queries").isZero();
        }
        if (!asserted.contains(StatType.INSERT)) {
            assertThat(actual.getEntityInsertCount()).as("unexpected inserts").isZero();
        }
        if (!asserted.contains(StatType.UPDATE)) {
            assertThat(actual.getEntityUpdateCount()).as("unexpected updates").isZero();
        }
        if (!asserted.contains(StatType.DELETE)) {
            assertThat(actual.getEntityDeleteCount()).as("unexpected deletes").isZero();
        }
        if (!asserted.contains(StatType.COLLECTION_FETCH)) {
            assertThat(actual.getCollectionFetchCount()).as("unexpected collection fetches").isZero();
        }
        return myself;
    }
}
