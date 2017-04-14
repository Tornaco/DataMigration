package org.newstand.datamigration.secure;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Nick@NewStand.org on 2017/4/14 16:20
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Getter
@Setter
@ToString(callSuper = true)
public class VersionCheckResult {
    private boolean hasLater;
    private VersionInfo versionInfo;
}
