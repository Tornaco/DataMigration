package org.newstand.datamigration.data.event;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Builder;

/**
 * Created by Nick@NewStand.org on 2017/3/29 16:25
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Getter
@Builder
@ToString
public class UserAction extends RealmObject {

    private String eventTitle;
    private String eventDescription;

    @PrimaryKey
    private long fingerPrint;

    private long date;

    public UserAction() {
    }

    private UserAction(String eventTitle, String eventDescription, long fingerPrint, long date) {
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.fingerPrint = fingerPrint;
        this.date = date;
    }

    public static UserAction from(UserAction in) {
        return UserAction.builder()
                .date(in.date)
                .eventDescription(in.eventDescription)
                .eventTitle(in.eventTitle)
                .fingerPrint(in.fingerPrint)
                .build();
    }
}
