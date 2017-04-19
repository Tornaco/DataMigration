package org.newstand.datamigration.provider;

import org.newstand.datamigration.R;

/**
 * Created by Nick@NewStand.org on 2017/4/19 10:34
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public enum ThemeColor implements ThemeResBinder {
    Default {
        @Override
        public int nameRes() {
            return R.string.name_def;
        }

        @Override
        public int colorRes() {
            return R.color.primary;
        }
    },
    Indigo {
        @Override
        public int nameRes() {
            return R.string.name_indigo;
        }

        @Override
        public int colorRes() {
            return R.color.indigo;
        }
    },
    Blue {
        @Override
        public int nameRes() {
            return R.string.name_blue;
        }

        @Override
        public int colorRes() {
            return R.color.blue;
        }
    },
    Red {
        @Override
        public int nameRes() {
            return R.string.name_red;
        }

        @Override
        public int colorRes() {
            return R.color.red;
        }
    },
    CoolApk {
        @Override
        public int nameRes() {
            return R.string.name_coolapk;
        }

        @Override
        public int colorRes() {
            return R.color.green;
        }
    },
    Purple {
        @Override
        public int nameRes() {
            return R.string.name_purple;
        }

        @Override
        public int colorRes() {
            return R.color.purple;
        }
    },
    Pink {
        @Override
        public int nameRes() {
            return R.string.name_pink;
        }

        @Override
        public int colorRes() {
            return R.color.pink;
        }
    },
    CoolDark {
        @Override
        public int nameRes() {
            return R.string.name_dark;
        }

        @Override
        public int colorRes() {
            return R.color.dark;
        }
    };
}
