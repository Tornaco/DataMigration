package org.newstand.datamigration.utils;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.model.ResBinder;

/**
 * Created by Nick@NewStand.org on 2017/4/18 18:36
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public enum SeLinuxState implements ResBinder {
    Enforcing {
        @Override
        public int nameRes() {
            return R.string.enforcing;
        }

        @Override
        public int iconRes() {
            return 0;
        }
    },
    Permissive {
        @Override
        public int nameRes() {
            return R.string.permissive;
        }

        @Override
        public int iconRes() {
            return 0;
        }
    },
    Unknown {
        @Override
        public int nameRes() {
            return R.string.unknown;
        }

        @Override
        public int iconRes() {
            return 0;
        }
    }
}
