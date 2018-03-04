package com.architect.spider.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.architect.spider.http.HttpClient;
import com.architect.spider.http.HttpResponse;
import com.architect.spider.model.ChapterModel;
import com.architect.spider.model.CourseModel;
import com.architect.spider.service.ICourseService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by BaiYuliang on 2017/7/31.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Controller
@RequestMapping("/grap")
public class SpiderController {

    /**
     * 课程
     */
    private static final String COURSE_URL =
            "http://member.med66.com/mapi/versionm/classroom/mycware/getUserWareBySubjectID?_t=2259881142&appkey=773037bc-db6a-49bb-bf08-75d8e00685e8&time=2017-08-11%2000:17:36&platformSource=0&fordown=&vflag=1&pkey=aa0b0fbb65f607613639f0453c7c1320&ltime=1502381846779&sid=0bh4boe8m01oj198onnlpi7i46&eduSubjectID=900813&version=4.0.7";

    @Resource
    private ICourseService spiderService;

    private String inflateNum(int number) {
        if (number < 10) {
            return "0" + number;
        }
        return "" + number;
    }

    private static String getChapterUrl(String code, int cwareID) {
        return "http://classx.med66.com/Netwangxiao/api/GetKcjy.aspx?version=4.0.7&_t=513973936" +
                "&appkey=773037bc-db6a-49bb-bf08-75d8e00685e8&id=" + code + "&key=fb9d9e8144ef84875e0f9c469a2a6da5" +
                "&cwareID=" + cwareID + "&keytime=2017-08-04%2000:01:19";

    }

    /**
     * 获取课程下的章节信息
     *
     * @param courseModel 课程
     * @return 课程下的章节
     */
    private List<ChapterModel> getChapters(CourseModel courseModel) throws Exception {
        List<ChapterModel> list = new ArrayList<ChapterModel>();
        int failCount = 0;
        for (int i = 0; i <= 30 && failCount < 4; i++) {
            for (int j = 0; j <= 30 && failCount < 4; j++) {
                String code = inflateNum(i) + inflateNum(j);
                HttpClient httpClient = new HttpClient();
                httpClient.addHeader("Authorization", "773037bc-db6a-49bb-bf08-75d8e00685e8|4.0.7");
                HttpResponse response = httpClient.get(getChapterUrl(code, courseModel.getCwareID()));
                if (response.getResultCode() == HttpResponse.RESULT_CODE_SUCCESS
                        && (parseRetCode(response.getContent()) == 1)) {
                    ChapterModel chapterModel = new ChapterModel();
                    chapterModel.setChapterCode(code);
                    chapterModel.setCwareId(courseModel.getCwareID());
                    chapterModel.setCourseTitle(courseModel.getMobileTitle());
                    chapterModel.setContent(parseNodeText(response.getContent()));
                    list.add(chapterModel);
                } else {
                    failCount++;
                }
            }
        }
        return list;
    }

    /**
     * 获取返回值
     */
    private static int parseRetCode(String content) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(content.getBytes()));
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression expr = xpath.compile("/Root/ReturnCode");
        return Integer.valueOf((String) expr.evaluate(doc, XPathConstants.STRING));
    }

    /**
     * 获取xml中的nodeText节点内容
     */
    private static String parseNodeText(String content) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(content.getBytes()));
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression expr = xpath.compile("//nodeText");
        NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < nodeList.getLength(); i++) {
            sb.append(nodeList.item(i).getTextContent());
        }
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        /**
         * 获取课程信息
         */
        HttpClient httpClient = new HttpClient();
        httpClient.addHeader("Authorization", "773037bc-db6a-49bb-bf08-75d8e00685e8|4.0.7");
        HttpResponse response = httpClient.get(COURSE_URL);
        System.out.println(response.getContent());
        JSONObject jsonObject = JSONObject.parseObject(response.getContent());
        List<CourseModel> list = JSONArray.parseArray(jsonObject.getString("cwList"), CourseModel.class);
        System.out.println(list);

        /**
         * 获取章节信息
         */
//        HttpClient httpClient = new HttpClient();
//        httpClient.addHeader("Authorization", "773037bc-db6a-49bb-bf08-75d8e00685e8|4.0.7");
//        HttpResponse response = httpClient.get(getChapterUrl("0101", 705753));
//        System.out.println(response.getContent());
//        System.out.println("" + parseRetCode(response.getContent()));
//        System.out.println(parseNodeText(response.getContent()));
    }

    @RequestMapping("/course")
    @ResponseBody
    public String course() {
        HttpClient httpClient = new HttpClient();
        httpClient.addHeader("Authorization", "773037bc-db6a-49bb-bf08-75d8e00685e8|4.0.7");
        HttpResponse response = httpClient.get(COURSE_URL);
        String ret = response.getErrorMsg();
        if (response.getResultCode() == HttpResponse.RESULT_CODE_SUCCESS) {
            JSONObject jsonObject = JSONObject.parseObject(response.getContent());
            List<CourseModel> list = JSONArray.parseArray(jsonObject.getString("cwList"), CourseModel.class);
            if (spiderService.addCourse(list)) {
                ret = "插入成功";
            } else {
                ret = "插入失败";
            }
        }
        return ret;
    }

    @RequestMapping("/{year}/chapter")
    @ResponseBody
    public String chapter(@PathVariable String year) throws Exception {
        CourseModel courseModel = new CourseModel();
        courseModel.setCYearName(year);
        List<CourseModel> courseList = spiderService.getCourseByYear(courseModel);
        boolean flag = true;
        for (CourseModel course : courseList) {
            List<ChapterModel> chapterList = getChapters(course);
            if (chapterList.size() > 0) {
                System.out.println("----插入课程" + course.getMobileTitle() + ",[" + course.getCwareID() + "]成功。-----");
                flag &= spiderService.addChapter(chapterList);
            } else {
                System.out.println("====插入课程" + course.getMobileTitle() + ",[" + course.getCwareID() + "]失败！=====");
                flag &= false;
            }
        }
        return flag ? "插入成功" : "插入失败";
    }

}