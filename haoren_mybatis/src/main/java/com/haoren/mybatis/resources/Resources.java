package com.haoren.mybatis.resources;

import java.io.InputStream;

public class Resources {

    public static InputStream getResourceAsStream(String path) {
        return Resources.class.getClassLoader().getResourceAsStream(path);
    }
}
