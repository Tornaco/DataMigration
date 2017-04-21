package org.newstand.datamigration.service.schedule;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.model.ResBinder;

/**
 * Created by Nick@NewStand.org on 2017/4/21 21:36
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public enum NetworkType implements ResBinder {

    NETWORK_TYPE_NONE {
        @Override
        public int nameRes() {
            return R.string.title_condition_network_type_none;
        }

        @Override
        public int iconRes() {
            return 0;
        }
    },
    /**
     * This job requires network connectivity.
     */
    NETWORK_TYPE_ANY {
        @Override
        public int nameRes() {
            return R.string.title_condition_network_type_any;
        }

        @Override
        public int iconRes() {
            return 0;
        }
    },
    /**
     * This job requires network connectivity that is unmetered.
     */
    NETWORK_TYPE_UNMETERED {
        @Override
        public int nameRes() {
            return R.string.title_condition_network_type_unmetered;
        }

        @Override
        public int iconRes() {
            return 0;
        }
    },
    /**
     * This job requires network connectivity that is not roaming.
     */
    NETWORK_TYPE_NOT_ROAMING {
        @Override
        public int nameRes() {
            return R.string.title_condition_network_type_mo_roaming;
        }

        @Override
        public int iconRes() {
            return 0;
        }
    }

}
