package com.architect.spring;

import com.architect.spider.http.HttpClient;
import com.architect.spider.http.HttpResponse;
import com.architect.spider.model.ChapterModel;
import com.architect.spider.service.ICourseService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
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
 * Created by BaiYuliang on 2017/8/4.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath*:/config/applicationContext.xml",
        "classpath*:/config/spring-mvc.xml"})
public class SpiderController {

    private static final int CWARE_ID = 706106;
    private static final String CODE = "0201";
    private static final String TITLE = "【应试技巧】考前复习安排";

    private static final String CHAPTER_URL =
            "http://classx.med66.com/Netwangxiao/api/GetKcjy.aspx?version=4.0.7&_t=1253713303" +
                    "&appkey=773037bc-db6a-49bb-bf08-75d8e00685e8&id=0201&key=ae44e46dcec2885a395a47110ed6215b" +
                    "&cwareID=706106&keytime=2017-08-12%2022:53:29";


    @Resource
    private ICourseService courseService;

    /**
     * 获取返回值
     */
    private int parseRetCode(String content) throws Exception {
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
    private String parseNodeText(String content) throws Exception {
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

    @Test
    public void addChapter() throws Exception {
        /**
         * 获取章节信息
         */
        HttpClient httpClient = new HttpClient();
        httpClient.addHeader("Authorization", "773037bc-db6a-49bb-bf08-75d8e00685e8|4.0.7");
        HttpResponse response = httpClient.get(CHAPTER_URL);
        if (response.getResultCode() == HttpResponse.RESULT_CODE_SUCCESS
                && (parseRetCode(response.getContent()) == 1)) {
            ChapterModel chapterModel = new ChapterModel();
            chapterModel.setChapterCode(CODE);
            chapterModel.setCwareId(CWARE_ID);
            chapterModel.setChapterTitle(TITLE);
            chapterModel.setContent(parseNodeText(response.getContent()).trim());
            System.out.println(response.getContent().trim());
            List<ChapterModel> list = new ArrayList<ChapterModel>();
            list.add(chapterModel);
            courseService.addChapter(list);
            System.out.println("插入数据成功");
        }
    }

}
