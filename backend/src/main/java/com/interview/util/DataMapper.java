package com.interview.util;

import org.mapstruct.Mapper;

import com.interview.dto.DataPayload;
import com.interview.entity.DataRecord;

@Mapper(componentModel = "spring")
public interface DataMapper {

    DataRecord dataPayloadToRecord(DataPayload payload);

    DataPayload dataRecordToPayload(DataRecord record);

}
