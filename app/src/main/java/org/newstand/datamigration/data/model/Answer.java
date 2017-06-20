package org.newstand.datamigration.data.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Nick on 2017/6/20 17:20
 */
@Getter
@Setter
@ToString
public class Answer {
    private String text;
    private String[] imageUrls;
}
