package org.newstand.datamigration.utils;

import org.junit.Assert;
import org.junit.Test;
import org.newstand.logger.Logger;
import org.newstand.logger.Settings;

/**
 * Created by Nick@NewStand.org on 2017/4/18 18:26
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
public class SeLinuxEnablerTest {
    @Test
    public void setState() throws Exception {
        Assert.assertTrue(SeLinuxEnabler.setState(SeLinuxState.Enforcing));
        Assert.assertTrue(SeLinuxEnabler.getSeLinuxState() == SeLinuxState.Enforcing);
        Assert.assertTrue(SeLinuxEnabler.setState(SeLinuxState.Permissive));
        Assert.assertTrue(SeLinuxEnabler.getSeLinuxState() == SeLinuxState.Permissive);
    }

    @Test
    public void getSeLinuxState() throws Exception {
        SeLinuxState seLinuxState = SeLinuxEnabler.getSeLinuxState();
        Logger.d("state = %s", seLinuxState.toString());
    }
}