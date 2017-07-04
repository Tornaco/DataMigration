package org.newstand.datamigration.practise;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Builder;

/**
 * Created by Nick on 2017/7/2 11:49
 */
@Setter
@Getter
@Builder
@ToString(of = {"title"})
public class Notification {

    private String title;
    private String content;
    private String icon;
    private String bigPicture;
    private String link;

    private long when;

    private boolean vibrate;
    private boolean ring;
}
