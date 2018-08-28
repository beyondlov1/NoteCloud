package com.beyond.utils;

import com.beyond.entity.Document;
import com.beyond.entity.Note;
import com.beyond.entity.Todo;
import com.thoughtworks.xstream.XStream;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

public class XmlUtils {

    private static ThreadLocal<XStream> tl = new ThreadLocal<XStream>();
    public static XStream getXStream() {
        if (tl.get()==null){
            XStream xStream = new XStream();
            xStream.alias("document",Document.class);
            xStream.alias("note",Note.class);
            xStream.alias("todo",Todo.class);
            xStream.alias("documents",List.class);
            xStream.useAttributeFor(Document.class,"id");
            xStream.useAttributeFor(Note.class,"id");
            xStream.useAttributeFor(Todo.class,"id");
            tl.set(xStream);
        }
        return tl.get();
    }

    public static void saveXml(String xml,String xmlPath) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(xmlPath);
            fileWriter.write(xml);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static void appendInRootXml(String xml, String xmlPath, String rootTag) {
        String suffix = "</"+rootTag+">";
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(xmlPath, "rw");
            long pos = new File(xmlPath).length() - suffix.length();
            if (pos==0){
                xml= "<"+rootTag+">"+xml;
            }
            randomAccessFile.seek(pos);
            randomAccessFile.write(xml.getBytes());
            randomAccessFile.write(suffix.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
