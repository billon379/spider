package com.architect.spider.dao;

import com.architect.spider.model.ChapterModel;
import com.architect.spider.model.CourseModel;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by BaiYuliang on 2017/7/31.
 */
@Repository
public interface ICourseDAO {

    /**
     * 插入课程
     */
    public int insertCourse(List<CourseModel> courseModel);

    /**
     * 添加章节
     */
    public int insertChapter(List<ChapterModel> chapterModel);

    /**
     * 查询课程
     */
    public List<CourseModel> queryCourseByYear(CourseModel courseModel);

    /**
     * 查询章节列表
     */
    public List<ChapterModel> queryChapter(ChapterModel chapterModel);


    /**
     * 查询章节详情
     */
    public ChapterModel queryChapterDetail(ChapterModel chapterModel);
}
