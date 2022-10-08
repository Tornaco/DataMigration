/*
 * Copyright (c) 2016 Nick Guo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.newstand.datamigration.ui;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.newstand.datamigration.R;
import org.newstand.datamigration.utils.Collections;
import org.newstand.logger.Logger;

import java.util.List;

public class FragmentController {

    private List<? extends Fragment> mPages;
    private FragmentManager mFragmentManager;

    private Fragment mCurrent;

    private int mDefIndex = 0;

    private int containerId = R.id.container;

    public FragmentController(FragmentManager manager, List<? extends Fragment> safeFragments, int id) {
        this.mFragmentManager = manager;
        this.mPages = safeFragments;
        this.containerId = id;
        init();
    }

    private void init() {
        FragmentManager fragmentManager = mFragmentManager;
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        List<Fragment> old = fragmentManager.getFragments();

        if (!Collections.isNullOrEmpty(old)) {
            for (Fragment fragment : old) {
                transaction.remove(fragment);
                Logger.v("Removed %s", fragment);
            }
        }

        for (Fragment fragment : mPages) {
            transaction.add(containerId, fragment, fragment.getClass().getSimpleName());
            transaction.hide(fragment);
        }

        transaction.commitAllowingStateLoss();
    }

    public void setDefaultIndex(int index) {
        mDefIndex = index;
    }

    public Fragment getCurrent() {
        return mCurrent == null ? mPages.get(mDefIndex) : mCurrent;
    }

    public void setCurrent(int index) {
        FragmentManager fragmentManager = mFragmentManager;
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.hide(getCurrent());
        Fragment current = mPages.get(index);
        transaction.show(current);
        transaction.commitAllowingStateLoss();
        mCurrent = current;
    }
}
