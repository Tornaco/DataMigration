package org.newstand.lib.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Builder;

/**
 * Created by Nick@NewStand.org on 2017/3/31 17:50
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Settings {
    private int logLvel;
    private LogAdapter logAdapter;
    private String tag;
}
