package com.star.pivot.common.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateDeserializer extends JsonDeserializer<Date> {

    private static final String[] DATE_FORMATS = {
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd",
        "yyyy/MM/dd HH:mm:ss",
        "yyyy/MM/dd",
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss.SSS",
        "yyyy-MM-dd'T'HH:mm:ss"
    };

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        String dateString = jsonParser.getText();
        
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        
        for (String format : DATE_FORMATS) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                sdf.setLenient(false); // 严格解析
                return sdf.parse(dateString);
            } catch (ParseException e) {
                // 尝试下一个格式
            }
        }
        
        throw new IOException("无法解析日期字符串: " + dateString + ", 支持的格式包括: " + String.join(", ", DATE_FORMATS));
    }
}