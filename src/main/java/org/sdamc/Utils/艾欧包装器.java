package org.sdamc.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/*
* 顾名思义，艾欧包装器就是一个包装器，用于包装艾欧
* As the name suggests, the I/O wrapper is a wrapper for I/O
* 名前が示すように、I/OラッパーはI/Oのラッパーです。
* Как следует из названия, оболочка ввода-вывода — это оболочка для ввода-вывода.
* */
@Slf4j
public class 艾欧包装器 {

    ObjectMapper mapper = new ObjectMapper();

    static 艾欧包装器 iobzq;

    public static 艾欧包装器 getInstance() {
        if (iobzq == null) {
            iobzq = new 艾欧包装器();
        }
        return iobzq;
    }

    public static <T> T readValue(HttpServletRequest req, Class<T> valueType) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(req.getInputStream(), valueType);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeValue(HttpServletResponse resp, Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            resp.setContentType("application/json");
            mapper.writeValue(resp.getOutputStream(), obj);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeValue(HttpServletResponse resp, Object obj, int status) {
        resp.setStatus(status);
        writeValue(resp, obj);
    }

}
