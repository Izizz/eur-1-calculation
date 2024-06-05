package com.example.eurcertificatecalc.calculation.service;

import com.example.eurcertificatecalc.calculation.data.entity.Attachment;
import com.example.eurcertificatecalc.calculation.model.AttachmentDto;
import com.example.eurcertificatecalc.calculation.model.SearchType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface AttachmentService {

    Attachment storeFile(MultipartFile multipartFile, Set<String> types) throws Exception;

    Attachment getAttachment(String fileName) throws Exception;

    Attachment getAttachment(UUID id);

    void deleteFile(String fileName);

    Attachment getIncomeFile();

    Page<Attachment> getDeclarations(PageRequest pageRequest);

    Page<AttachmentDto> getFilesByType(String type, PageRequest pageRequest);

    Page<AttachmentDto> getFiles(String name, PageRequest pageRequest);

    List<AttachmentDto> findAll(String search, PageRequest pageRequest, SearchType searchType);

}