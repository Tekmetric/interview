package com.interview.common.events

import org.springframework.modulith.events.Externalized

/*
  The events in this file are marked as Externalized, which sets them up to be sent by Modulith
  to some messaging broker such as Kafka. This is as far as the externalization is taken in this PR.
*/
@Externalized("widget.created::#{id()}")
data class WidgetCreatedEvent(val id: Long, val name: String, val description: String)

@Externalized("widget.deleted::#{id()}")
data class WidgetDeletedEvent(val id: Long)

@Externalized("widget.updated::#{id()}")
data class WidgetUpdatedEvent(val id: Long, val name: String, val description: String)
