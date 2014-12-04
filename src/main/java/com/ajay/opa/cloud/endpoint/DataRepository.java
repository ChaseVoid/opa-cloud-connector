package com.ajay.opa.cloud.endpoint;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajay on 05/12/14.
 */
@Component
public class DataRepository {
    private static final List<String> data = new ArrayList<String>();

    @PostConstruct
    public void initData() {
        data.add("test1");
        data.add("test2");
    }

    public String find(String name) {
        Assert.notNull(name);
        String result = null;
        for (String d : data) {
            if (name.equals(d)) {
                result = d;
            }
        }
        return result;
    }
}
