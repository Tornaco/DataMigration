package org.newstand.datamigration.worker.transport;

import android.support.annotation.StringRes;

import org.newstand.datamigration.R;

import lombok.Getter;

/**
 * Created by guohao4 on 2017/7/10.
 */

public enum RecordEvent {
    FileCopy(R.string.transport_event_description_copy_file),
    CopyApk(R.string.transport_event_description_copy_apk),
    CopyData(R.string.transport_event_description_copy_data),
    InstallApk(R.string.transport_event_description_install_apk),
    InstallApkWaitForResult(R.string.transport_event_description_install_apk_wait_for_res),
    InstallData(R.string.transport_event_description_install_data),
    InstallExtraData(R.string.transport_event_description_install_extra_data),
    CreateDir(R.string.transport_event_description_create_dir),
    Init(R.string.transport_event_description_init),
    Insert(R.string.transport_event_description_insert),
    WaitForSMSDefApp(R.string.transport_event_description_wait_for_def_sms_app);


    @Getter
    private
    @StringRes
    int description;

    RecordEvent(int description) {
        this.description = description;
    }
}
