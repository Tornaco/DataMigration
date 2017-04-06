package org.newstand.datamigration.data.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Builder;

/**
 * Created by Nick@NewStand.org on 2017/4/6 15:15
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Getter
@Setter
@Builder
@ToString
public class License {
    String title;
    String author;
    String description;
    String version;
    String url;
    String fullLicense;
    String assetsDir;
}
