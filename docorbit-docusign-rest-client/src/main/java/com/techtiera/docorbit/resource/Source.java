package com.techtiera.docorbit.resource;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Source {

    private String name;
    private String version;
    private List<EnvelopReportDetails> envelops = new ArrayList<>();
    private List<TemplateReportDetails> templates = new ArrayList<>();

}
