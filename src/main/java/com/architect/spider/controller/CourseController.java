package com.architect.spider.controller;

import com.architect.spider.model.ChapterModel;
import com.architect.spider.model.CourseModel;
import com.architect.spider.service.ICourseService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by BaiYuliang on 2017/8/12.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Controller
@RequestMapping("/course")
public class CourseController {

    @Resource
    private ICourseService courseService;


    @RequestMapping("/class/")
    @ResponseBody
    public List<CourseModel> classList() {
        CourseModel courseModel = new CourseModel();
        courseModel.setCYearName("2017");
        List<CourseModel> list = courseService.getCourseByYear(courseModel);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setRowNum(i + 1);
        }
        return list;
    }

    @RequestMapping("/{class}/chapter/")
    @ResponseBody
    public List<ChapterModel> chapter(@PathVariable("class") int classId) {
        ChapterModel chapterModel = new ChapterModel();
        chapterModel.setCwareId(classId);
        List<ChapterModel> list = courseService.getChapter(chapterModel);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setRowNum(i + 1);
        }
        return list;
    }

    @RequestMapping("/{class}/chapter/{id}")
    @ResponseBody
    public ChapterModel chapter(@PathVariable("class") int classId, @PathVariable("id") int id) {
        ChapterModel chapterModel = new ChapterModel();
        chapterModel.setCwareId(classId);
        chapterModel.setId(id);
        return courseService.getChapterDetail(chapterModel);
    }

}
