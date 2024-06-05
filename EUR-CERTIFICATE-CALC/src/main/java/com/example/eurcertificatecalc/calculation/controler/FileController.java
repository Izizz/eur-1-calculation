package com.example.eurcertificatecalc.calculation.controler;

import com.example.eurcertificatecalc.calculation.data.entity.Attachment;
import com.example.eurcertificatecalc.calculation.model.AttachmentDto;
import com.example.eurcertificatecalc.calculation.model.FileUploadResponse;
import com.example.eurcertificatecalc.calculation.model.SearchType;
import com.example.eurcertificatecalc.calculation.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final AttachmentService attachmentService;

    @PostMapping("/upload")
    public List<FileUploadResponse> uploadFiles(@RequestParam("files") MultipartFile[] files, @RequestParam("types") Set<String> types) {

        log.info("Uploading files: {}", Arrays.stream(files).map(MultipartFile::getName));

        List<FileUploadResponse> response = new ArrayList<>();

        for (MultipartFile file : files) {
            Attachment attachment = null;
            try {
                attachment = attachmentService.storeFile(file, types);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            if (attachment != null) {
                String downloadURL = ServletUriComponentsBuilder
                        .fromCurrentContextPath()
                        .path("api/v1/file/download/")
                        .path(String.valueOf(attachment.getFileName())
                        ).toUriString();

                response.add(FileUploadResponse.builder()
                        .fileName(attachment.getFileName())
                        .size(file.getSize())
                        .fileType(file.getContentType())
                        .fileDownloadUri(downloadURL)
                        .build());
            }
        }

        return response;
    }

    @GetMapping("/all")
    public List<AttachmentDto> getAllFiles(@RequestParam(value = "page", defaultValue = "0") int page,
                                              @RequestParam(value = "size", defaultValue = "10") int size,
                                              @RequestParam(value = "search", defaultValue = "") String search,
                                              @RequestParam(value = "searchBy", defaultValue = "BY_EMPTY_STRING") String searchBy)  {
        log.info("Getting all files ");
        PageRequest pageRequest = PageRequest.of(page, size);
        var searchType = SearchType.valueOf(searchBy);

        return attachmentService.findAll(search, pageRequest, searchType);
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) throws Exception {
        var attachment = attachmentService.getAttachment(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + attachment.getFileName() + "\"")
                .body(new ByteArrayResource(attachment.getData()));
    }
}
