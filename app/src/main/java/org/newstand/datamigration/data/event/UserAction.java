package org.newstand.datamigration.data.event;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Builder;

/**
 * Created by Nick@NewStand.org on 2017/3/29 16:25
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Getter
@Setter
@Builder
@ToString
public class UserAction {

    private int id;

    private String eventTitle;
    private String eventDescription;

    private long fingerPrint;

    private long date;

    public UserAction() {
    }

    public UserAction(int id, String eventTitle, String eventDescription, long fingerPrint, long date) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserAction that = (UserAction) o;

        if (fingerPrint != that.fingerPrint) return false;
        if (date != that.date) return false;
        if (eventTitle != null ? !eventTitle.equals(that.eventTitle) : that.eventTitle != null)
            return false;
        return eventDescription != null ? eventDescription.equals(that.eventDescription) : that.eventDescription == null;

    }

    @Override
    public int hashCode() {
        int result = eventTitle != null ? eventTitle.hashCode() : 0;
        result = 31 * result + (eventDescription != null ? eventDescription.hashCode() : 0);
        result = 31 * result + (int) (fingerPrint ^ (fingerPrint >>> 32));
        result = 31 * result + (int) (date ^ (date >>> 32));
        return result;
    }
}
