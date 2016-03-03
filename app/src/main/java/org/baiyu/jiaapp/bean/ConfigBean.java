package org.baiyu.jiaapp.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by baiyu on 2015-9-18.
 */
public class ConfigBean extends DataSupport {
    private String key;
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
