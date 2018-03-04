package com.architect.spider.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by BaiYuliang on 2017/8/3.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ChapterModel {

    private int rowNum;
    private Integer id;
    private String chapterTitle;
    private String chapterCode;
    private String content;
    private Integer cwareId;
    private String courseTitle;

    @Override
    public String toString() {
        return "ChapterModel{" +
                "id=" + id +
                ", chapterTitle='" + chapterTitle + '\'' +
                ", chapterCode='" + chapterCode + '\'' +
                ", content='" + content + '\'' +
                ", cwareId=" + cwareId +
                ", courseTitle='" + courseTitle + '\'' +
                '}';
    }
}
