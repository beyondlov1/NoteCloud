package com.beyond.utils;

import org.markdownj.MarkdownProcessor;

public class MarkDownUtils {
    public static String convertMarkDownToHtml(String markdown){
        MarkdownProcessor markdownProcessor = new MarkdownProcessor();
        return markdownProcessor.markdown(markdown);
    }
}
