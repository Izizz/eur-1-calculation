package com.example.eurcertificatecalc.calculation.service.impl;

import com.example.eurcertificatecalc.calculation.data.entity.Attachment;
import com.example.eurcertificatecalc.calculation.data.entity.Type;
import com.example.eurcertificatecalc.calculation.data.repository.AttachmentRepository;
import com.example.eurcertificatecalc.calculation.exception.WrongFileNameException;
import com.example.eurcertificatecalc.calculation.model.AttachmentDto;
import com.example.eurcertificatecalc.calculation.model.SearchType;
import com.example.eurcertificatecalc.calculation.service.AttachmentService;
import com.example.eurcertificatecalc.calculation.service.TypeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final TypeService typeService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public Attachment storeFile(MultipartFile file, Set<String> types) throws Exception {

        Set<Type> typesForFile = new HashSet<>();
        try {
            types.forEach(x -> {
                var type = typeService.getType(x);
                typesForFile.add(type);
            });
        } catch (EntityNotFoundException e) {
            log.error(e.getMessage());
        }

        var fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        if (fileName.contains("..")) {
            throw new WrongFileNameException("Filename contains invalid path sequence");
        }

        var attachment = Attachment.builder()
                .fileName(fileName)
                .fileType(file.getContentType())
                .data(file.getBytes())
                .types(typesForFile)
                .build();

        var isInDatabase = existsByFileName(attachment.getFileName());

        if (isInDatabase) {
            attachmentRepository.deleteFromAttachmentsTypes(attachment.getId());
            attachmentRepository.deleteByFileName(attachment.getFileName());
        }

        return attachmentRepository.save(attachment);
    }

    @Override
    public Attachment getAttachment(String fileName) {
        return attachmentRepository.findByFileName(fileName).orElseThrow(() -> new EntityNotFoundException("File was not found: " + fileName));

    }

    @Override
    public Attachment getAttachment(UUID id) {
        return attachmentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("File was not found: " + id));
    }

    @Override
    public void deleteFile(String fileName) {
        var optionalAttachment = attachmentRepository.findByFileName(fileName);
        if (optionalAttachment.isPresent()) {
            var attachment = optionalAttachment.get();
            attachmentRepository.deleteFromAttachmentsTypes(attachment.getId());
            attachmentRepository.deleteByFileName(fileName);
        }
    }

    @Override
    @Transactional
    public Attachment getIncomeFile() {
        return attachmentRepository.findByTypeName("INCOME_FILE");
    }

    @Override
    public Page<Attachment> getDeclarations(PageRequest pageRequest) {
        return attachmentRepository.findByTypesTypeName("DECLARATION_PDF", pageRequest);
    }

    @Override
    public Page<AttachmentDto> getFilesByType(String type, PageRequest pageRequest) {
        return attachmentRepository.findByTypesTypeName(type, pageRequest).map(file->objectMapper.convertValue(file,AttachmentDto.class));
    }

    @Override
    public Page<AttachmentDto> getFiles(String name, PageRequest pageRequest) {
        ObjectMapper objectMapper = new ObjectMapper();

        return attachmentRepository.findByTypesTypeName(name, pageRequest).map((x)->objectMapper.convertValue(x,AttachmentDto.class));
    }

    @Override
    public List<AttachmentDto> findAll(String search, PageRequest pageRequest, SearchType searchType) {
        return attachmentRepository.findAll().stream().map(AttachmentDto::instance).toList();

    }

    private boolean existsByFileName(String fileName) {
        var result = attachmentRepository.findByFileName(fileName);

        return result.isPresent();
    }

}
