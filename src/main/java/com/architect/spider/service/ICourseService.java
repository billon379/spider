package com.architect.spider.service;

import com.architect.spider.model.ChapterModel;
import com.architect.spider.model.CourseModel;

import java.util.List;

/**
 * Created by BaiYuliang on 2017/7/31.
 */
public interface ICourseService {

    /**
     * 插入课程
     */
    public boolean addCourse(List<CourseModel> courseModel);


    /**
     * 添加章节
     */
    public boolean addChapter(List<ChapterModel> chapterModel);

    /**
     * 查询课程
     */
    public List<CourseModel> getCourseByYear(CourseModel courseModel);

    /**
     * 查询章节列表
     */
    public List<ChapterModel> getChapter(ChapterModel chapterModel);


    /**
     * 查询章节详情
     */
    public ChapterModel getChapterDetail(ChapterModel chapterModel);
}
