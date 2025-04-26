package Model.utils;

import Controller.Infore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;


public class JSONUtils {
    //获取JSON字符串
    public static String getJSONstr(HttpServletRequest req) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String str = null;
        BufferedReader bfr = req.getReader();
        StringBuilder strb = new StringBuilder();
        while ((str = bfr.readLine())!= null){
            strb.append(str);
        }
        return strb.toString();
    }
    //获取请求体字符串
    public static String getStr(HttpServletRequest req) throws IOException {
        BufferedReader bufferedReader = req.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line =  bufferedReader.readLine()) != null){
            sb.append(line);
        }
        String str = sb.toString();
        return str;
    }
    //把接受到的数据转化成对象
    public static <T> T getObject(HttpServletRequest req, Class<T> clazz) throws IOException {
        String json = JSONUtils.getJSONstr(req);
        System.out.println(json);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        T t = objectMapper.readValue(json, clazz);
        return t;
    }
    //数据转换成JSON字符串
    public static <T> String toJSON(T t) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String json = objectMapper.writeValueAsString(t);
        return json;
    }
    //通过JSON字符串获取对象
    public static <T> T getObjectbyjson(String json, Class<T> clazz) throws IOException {
        System.out.println(json);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        T t = objectMapper.readValue(json, clazz);
        return t;
    }
}
