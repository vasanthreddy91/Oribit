package com.techtiera.docorbit.batch.config;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

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
@XmlRootElement(name = "template")
@XmlAccessorType(XmlAccessType.FIELD)
public class TemplateWrapper {

    private String templateId;
    private String uri;
    private String name;
    private String description;
    private String created;
    private String lastModified;
    private String lastUsed;
    private Integer pageCount;
    private String folderId;
    private String folderName;

    @JsonProperty("folderIds")
    private List<String> folderIdList;

    private UserRecord owner;
    private UserRecord lastModifiedBy;

//    @JsonProperty("documents")
//    private List<DocumentRecord> documents;
    @XmlElementWrapper(name = "documents")
    @XmlElement(name = "document")
    private List<DocumentRecord> documents;

    private RecipientWrapper recipients;

    private String emailSubject;
    private String emailBlurb;
    private String signingLocation;
    private Boolean enableWetSign;
    private Boolean allowMarkup;
    private Boolean allowReassign;

    // Inner classes for Document and Recipients
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class DocumentRecord {
        private String documentId;
        private String name;
        private String uri;
        private Integer order;
        private Integer pages;
        private String display;
        private Boolean includeInDownload;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class RecipientWrapper {
        private Integer recipientCount;

        @XmlElementWrapper(name = "signers")
        @XmlElement(name = "signer")
        private List<SignerRecord> signers;
//        @JsonProperty("signers")
//        private List<SignerRecord> signers;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class SignerRecord {
        private String name;
        private String email;
        private String recipientId;
        private Integer routingOrder;
        private String recipientType;
    }
}
