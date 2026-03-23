package com.techtiera.docorbit.resource;

import java.util.ArrayList;

import com.techtiera.docorbit.config.PropertiesHolder;


public class TemplateXmlHolder {

    private static final Source SOURCE;

    static {
        SOURCE = new Source();
        SOURCE.setName("DocuSign");
        SOURCE.setVersion(PropertiesHolder.getDocusignversion());
        SOURCE.setTemplates(new ArrayList<>());
    }

    public static Source getSource() {
        return SOURCE;
    }
}