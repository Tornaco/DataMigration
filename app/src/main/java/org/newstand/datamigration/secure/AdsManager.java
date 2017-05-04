package org.newstand.datamigration.secure;

/**
 * Created by Nick@NewStand.org on 2017/5/2 16:41
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public enum AdsManager implements AdsInterface {
    Ali {
        @Override
        public String appId() {
            return "100051094";
        }

        @Override
        public String appSecret() {
            return "430a768ba1631863fd48c6e31637d08f";
        }

        @Override
        public String banner() {
            return "0a15b46aab96477ea49bf49760631b19";
        }

        @Override
        public String appWall() {
            return "b0c6c7001cae9a439fdfcd88deec95e9";
        }

        @Override
        public String insert() {
            return "a9cbfbbe7e933abb3074245bef8f4a34";
        }
    },
}
