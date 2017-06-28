package org.newstand.datamigration.service;

import org.newstand.datamigration.common.ActionListener;

/**
 * Created by Nick on 2017/6/27 21:54
 */

public interface DataCompresser {

    void compressAsync(String src, String dest, ActionListener<Boolean> listener);
}
