package com.app.tools;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Author chzjy
 * Date 2016/12/19.
 * 解析版本信息
 */

public class VersionXmlParse {
    public static HashMap<String, String> parseXml(InputStream inStream) throws Exception {
        HashMap<String, String> hashMap = new HashMap<String, String>();

        // 实例化一个文档构建器工厂
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // 通过文档构建器工厂获取一个文档构建器
        DocumentBuilder builder = factory.newDocumentBuilder();
        // 通过文档通过文档构建器构建一个文档实例
        Document document = builder.parse(inStream);
        //获取XML文件根节点
        Element root = document.getDocumentElement();
        String type = root.getTagName();
        switch (type) {
            case "update":
                Element versionElement = (Element) root.getElementsByTagName("version").item(0);
                Element nameElement = (Element) root.getElementsByTagName("name").item(0);
                Element pathElement = (Element) root.getElementsByTagName("path").item(0);
                String version = versionElement.getFirstChild().getNodeValue();
                String name = nameElement.getFirstChild().getNodeValue();
                String path = pathElement.getFirstChild().getNodeValue();
                hashMap.put("version", version);
                hashMap.put("name", name);
                hashMap.put("path", path);
                break;
        }
        return hashMap;
    }
}
