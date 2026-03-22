package com.interview.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import com.interview.persistence.enums.SupportingDocumentType;

@Entity
@Table(
    name = "supporting_document",
    indexes = {
        @Index(name = "idx_doc_application_id", columnList = "application_id")
    }
)
@Getter
@Setter
public class SupportingDocument extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "application_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_doc_application_id")
    )
    private CreditApplication application;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 40)
    private SupportingDocumentType documentType;

    @NotNull
    @Column(name = "object_key", nullable = false, length = 500)
    private String objectKey;

    @Column(name = "file_name")
    private String fileName;
}
