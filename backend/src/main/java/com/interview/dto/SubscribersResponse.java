package com.interview.dto;

import java.util.List;

public record SubscribersResponse(
    Integer totalCount,
    List<SubscriberDto> subscribers
) {
    public static SubscribersResponse of(List<SubscriberDto> subscribers) {
        return new SubscribersResponse(subscribers.size(), subscribers);
    }
}
