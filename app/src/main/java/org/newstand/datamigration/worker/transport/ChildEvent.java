package org.newstand.datamigration.worker.transport;

import android.support.annotation.StringRes;

import org.newstand.datamigration.R;

import lombok.Getter;

/**
 * Created by guohao4 on 2017/7/10.
 */

public enum ChildEvent {
    FileCopy(R.string.transport_event_description_copy_file),
    CopyApk(R.string.transport_event_description_copy_apk),
    CopyData(R.string.transport_event_description_copy_data),
    InstallApk(R.string.transport_event_description_install_apk),
    InstallData(R.string.transport_event_description_install_data),
    InstallExtraData(R.string.transport_event_description_install_extra_data);

    @Getter
    private
    @StringRes
    int description;

    ChildEvent(int description) {
        this.description = description;
    }
}
