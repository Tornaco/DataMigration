package org.newstand.datamigration.worker.transport;

import android.support.annotation.StringRes;

import org.newstand.datamigration.R;

import lombok.Getter;

/**
 * Created by guohao4 on 2017/7/10.
 */

public enum TransportEvent {
    FileCopy(R.string.transport_event_description_copy);

    @Getter
    private
    @StringRes
    int description;

    TransportEvent(int description) {
        this.description = description;
    }
}
