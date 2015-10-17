package com.ajay.opa.cloud.endpoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajay on 05/12/14.
 */
public class DataRepository {
    private static final List<String> data = new ArrayList<>();

    public DataRepository() {
        initData();
    }

    public void initData() {
        data.add("test1");
        data.add("test2");
    }

    public String find(String name) {
        String result = null;
        for (String d : data) {
            if (name.equals(d)) {
                result = d;
            }
        }
        return result;
    }
}
