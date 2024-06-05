package com.example.eurcertificatecalc.calculation.model;

import com.example.eurcertificatecalc.calculation.data.entity.Attachment;
import com.example.eurcertificatecalc.calculation.data.entity.Type;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentDto {
    private UUID id;
    private String fileName;
    private String fileType;
    private Set<Type> types;
    private long size;

    public static AttachmentDto instance(Attachment attachment) {
        return AttachmentDto.builder()
                .id(attachment.getId())
                .fileName(attachment.getFileName())
                .fileType(attachment.getFileType())
                .types(attachment.getTypes())
                .size(attachment.getSize())
                .build();
    }
}
