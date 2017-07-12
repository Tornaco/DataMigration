package org.newstand.datamigration.data.event;

import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Builder;

@Builder
@Getter
@ToString
public class TransportEventRecord {
    private long when;

    private DataCategory category;

    private DataRecord dataRecord;

    private String errMessage;
    private String errTrace;

    private boolean success;
}
