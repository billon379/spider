package com.architect.spider.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by BaiYuliang on 2017/7/30.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CourseModel {

    private Integer rowNum;
    private Long id;
    private Integer boardID;
    private String mobileTitle;
    private String cwareName;
    private String teacherName;
    private String cYearName;
    private Integer cwareClassID;
    private Integer mobileCourseOpen;
    private Integer cwareID;
    private String cwareTitle;

    private Integer num;


    @Override
    public String toString() {
        return "CourseModel{" +
                "id=" + id +
                ", boardID=" + boardID +
                ", mobileTitle='" + mobileTitle + '\'' +
                ", cwareName='" + cwareName + '\'' +
                ", teacherName='" + teacherName + '\'' +
                ", cYearName='" + cYearName + '\'' +
                ", cwareClassID=" + cwareClassID +
                ", mobileCourseOpen=" + mobileCourseOpen +
                ", cwareID=" + cwareID +
                ", cwareTitle='" + cwareTitle + '\'' +
                '}';
    }

}
