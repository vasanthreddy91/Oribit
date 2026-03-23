package com.techtiera.docorbit.batch.config;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "envelop")
@XmlAccessorType(XmlAccessType.FIELD)
public class EnvelopeListWrapper {

    
        private String Name;

        private String status;
        private String envelopeId;
        private String envelopeUri;

        private String createdDateTime;
        private String lastModifiedDateTime;
        private String sentDateTime;
        private String completedDateTime;

        private SenderRecord sender;

        @XmlElementWrapper(name = "documents")
        @XmlElement(name = "document")
        private List<DocumentRecord> documents;
    

    // ---------------- Sender ----------------

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class SenderRecord {

        private String senderName;
        private String senderUserId;
        private String senderAccountId;
        private String senderEmail;
    }

    // ---------------- Document ----------------

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class DocumentRecord {

        private String documentId;
        private String documentIdGuid;
        private String documentName;
        private String documentType;
        private String documentUri;
        private Integer order;

        @XmlElementWrapper(name = "pages")
        @XmlElement(name = "page")
        private List<PageRecord> pages;
    }

    // ---------------- Page ----------------

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class PageRecord {

        private String pageId;
        private Integer sequence;
        private Integer height;
        private Integer width;
        private Integer dpi;
    }
}
