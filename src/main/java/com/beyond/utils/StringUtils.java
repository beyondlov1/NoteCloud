package com.beyond.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    public static boolean isEmpty(String string){
        return string == null || "".equals(string);
    }

    public static List<String> getUrls(String source){
        List<String> list = new ArrayList<>();
        Pattern pattern = Pattern.compile("(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]");
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()){
            String string = matcher.group();
            list.add(string);
        }
        return list;
    }

    public static String replaceUrlsToMarkDownStyle(String source){
        Pattern pattern = Pattern.compile("(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]");
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()){
            String string = matcher.group();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("[");
            stringBuilder.append(string);
            stringBuilder.append("]");
            stringBuilder.append("(");
            stringBuilder.append(string);
            stringBuilder.append(")");
            source = source.replace(string, stringBuilder.toString());
        }
        return source;
    }

    public static void main(String[] args){
        StringUtils.replaceUrlsToMarkDownStyle("fadfdhttp://www.cnblogs.com/speeding/p/5097790.html gfskjf https://www.google.com/search?ei=NXJ5W5O3GerU0gLR5Y6ABw&q=htmlEditor+%E8%8E%B7%E5%8F%96%E5%8E%9F%E5%A7%8B%E5%86%85%E5%AE%B9+javafx&oq=htmlEditor+%E8%8E%B7%E5%8F%96%E5%8E%9F%E5%A7%8B%E5%86%85%E5%AE%B9+javafx&gs_l=psy-ab.3...6511.10569.0.10845.9.8.1.0.0.0.530.1465.2-1j2j0j1.4.0....0...1.1j4.64.psy-ab..4.0.0....0.seFB-EzbT-E");
    }

    public static String cutAndPretty(String content, int i) {
        if (content.replaceAll(" ","").length()>i){
            return content.substring(0,i)+"...";
        }else {
            return content;
        }
    }

    /**
     * 返回非空字符串， 如果为空则转化为 “”
     * @param object
     * @return
     */
    public static String getNotNullString(Object object){
        return String.valueOf(object==null?"":object);
    }

}
