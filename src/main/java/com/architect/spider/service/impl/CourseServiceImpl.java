package com.architect.spider.service.impl;

import com.architect.spider.dao.ICourseDAO;
import com.architect.spider.model.ChapterModel;
import com.architect.spider.model.CourseModel;
import com.architect.spider.service.ICourseService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by BaiYuliang on 2017/7/31.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Service
public class CourseServiceImpl implements ICourseService {

    @Resource
    private ICourseDAO courseDAO;

    public void setCourseDAO(ICourseDAO courseDAO) {
        this.courseDAO = courseDAO;
    }

    /**
     * 插入课程
     */
    public boolean addCourse(List<CourseModel> courseModel) {
        return courseDAO.insertCourse(courseModel) == courseModel.size();
    }

    /**
     * 添加章节
     */
    public boolean addChapter(List<ChapterModel> chapterModel) {
        return courseDAO.insertChapter(chapterModel) == chapterModel.size();
    }

    /**
     * 查询课程
     */
    public List<CourseModel> getCourseByYear(CourseModel courseModel) {
        return courseDAO.queryCourseByYear(courseModel);
    }

    /**
     * 查询章节列表
     */
    public List<ChapterModel> getChapter(ChapterModel chapterModel) {
        return courseDAO.queryChapter(chapterModel);
    }


    /**
     * 查询章节详情
     */
    public ChapterModel getChapterDetail(ChapterModel chapterModel) {
        return courseDAO.queryChapterDetail(chapterModel);
    }
}
