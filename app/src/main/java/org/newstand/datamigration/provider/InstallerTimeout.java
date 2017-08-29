package org.newstand.datamigration.provider;

/**
 * Created by Nick on 2017/7/1 16:54
 */

public enum InstallerTimeout {

    Long(5 * 60 * 1000),
    Medium(3 * 60 * 1000),
    Short(60 * 1000),
    VeryShort(30 * 1000),
    VeryVeryShort(15 * 1000);


    public long timeMills;

    InstallerTimeout(long timeMills) {
        this.timeMills = timeMills;
    }
}