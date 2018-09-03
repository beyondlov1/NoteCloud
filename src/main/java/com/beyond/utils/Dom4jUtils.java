package com.beyond.utils;

import com.sun.istack.internal.Nullable;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.dom4j.*;
import org.jaxen.NamespaceContext;

import java.util.*;

public class Dom4jUtils {

    public static Node getNode(String xmlString, Map<String, String> namespaceUris, String xPath) {
        try {
            Document document = DocumentHelper.parseText(xmlString);
            XPath xpath = document.createXPath(xPath);
            if (namespaceUris != null && !namespaceUris.isEmpty())
                xpath.setNamespaceURIs(namespaceUris);
            return xpath.selectSingleNode(document);
        } catch (DocumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getNodeText(String xmlString, Map<String, String> namespaceUris, String xPath) {
        Node node = getNode(xmlString, namespaceUris, xPath);
        if (node != null) {
            return node.getText();
        }
        return null;
    }



    /**
     * 获取所有节点（所有）
     *
     * @param xmlString
     * @return
     */
    public static List<Node> getAllNode(String xmlString) {
        List<Node> list = new ArrayList<>();
        try {
            Document document = DocumentHelper.parseText(xmlString);
            Element rootElement = document.getRootElement();
            getChildElement(rootElement, list);
            return list;
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private static void getChildElement(Element element, List<Node> list) {
        Iterator<Node> nodeIterator = element.nodeIterator();
        while (nodeIterator.hasNext()) {
            Node next = nodeIterator.next();
            if (next instanceof Element) {
                Element element1 = (Element) next;
                list.add(next);
                getChildElement(element1, list);
            }
        }
    }

    public static String getNodeText(String content, String s) {
        Map<String,String> namespaceUris = new HashMap<>();
        Namespace namespace = DavConstants.NAMESPACE;
        namespaceUris.put(namespace.getPrefix(),namespace.getURI());
        return getNodeText(content, namespaceUris, s);
    }

    public static void main(String[] args) {

        Map<String, String> map = new HashMap<>();
        map.put("jaxws", "http://cxf.apache.org/jaxws");
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>   \n" +
                "<beans xmlns=\"http://www.springframework.org/schema/beans\"  \n" +
                "              xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  \n" +
                "              xmlns:jaxws=\"http://cxf.apache.org/jaxws\"  \n" +
                "              xsi:schemaLocation=\"  \n" +
                "                  http://www.springframework.org/schema/beans  \n" +
                "                  http://www.springframework.org/schema/beans/spring-beans.xsd  \n" +
                "                  http://cxf.apache.org/jaxws  \n" +
                "                  http://cxf.apache.org/schemas/jaxws.xsd\"  \n" +
                "                  default-autowire=\"byName\" default-lazy-init=\"true\">  \n" +
                "    <import resource=\"classpath:META-INF/cxf/cxf.xml\" />  \n" +
                "    <import resource=\"classpath:META-INF/cxf/cxf-servlet.xml\" />  \n" +
                "    <import resource=\"classpath:META-INF/cxf/cxf-extension-soap.xml\" />  \n" +

                "    <bean id=\"jaxWsServiceFactoryBean\" class=\"org.apache.cxf.jaxws.support.JaxWsServiceFactoryBean\" scope=\"prototype\">  \n" +
                "            <property name=\"wrapped\" value=\"true\"/>  \n" +
                "            <property name=\"dataBinding\" ref=\"aegisBean\"/>  \n" +
                "        <jaxws:endpoint id=\"WSpersonService\" implementor=\"#personService\" address=\"/PersonService\">  \n" +
                "          <jaxws:serviceFactory> fdafd  </jaxws:serviceFactory>  \n" +
                "     </jaxws:endpoint>  \n" +
                "            <property name=\"serviceConfigurations\">  \n" +
                "               <list>  \n" +
                "                 <bean class=\"org.apache.cxf.jaxws.support.JaxWsServiceConfiguration\"/>  \n" +
                "                 <bean class=\"org.apache.cxf.aegis.databinding.AegisServiceConfiguration\"/>  \n" +
                "                 <bean class=\"org.apache.cxf.service.factory.DefaultServiceConfiguration\"/>  \n" +
                "               </list>  \n" +
                "             </property>  \n" +

                "     </bean>  \n" +
                "     <_bean id=\"aegisBean\" class=\"org.apache.cxf.aegis.databinding.AegisDatabinding\"/>  \n" +
                "     \n" +
                "<test/>" +
                "</beans>  ";

//        Node node = Dom4jUtils.getNode(xml, map, "*");
//        System.out.println(node);

//        List<Node> allElement = getAllNode(xml);
//        for (Node element : allElement) {
//            if (element instanceof Element) {
//                Element element1 = (Element) element;
//                System.out.println(element1.getName() + " |" + element1.getText() + "|");
//            }
//        }


        String cc = getNodeText(xml, "//text");
        System.out.println(cc);
    }

}
