package com.example.eurcertificatecalc.calculation.data.repository;

import com.example.eurcertificatecalc.calculation.data.entity.Attachment;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {

    @Transactional
    Optional<Attachment> findByFileName(String fileName);

    @Transactional
    void deleteByFileName(String fileName);

    boolean existsAttachmentById(UUID id);

    @Modifying
    @Transactional
    @Query(value = "delete from attachments_types where attachment_id = :attachment_id", nativeQuery = true)
    void deleteFromAttachmentsTypes(@Param("attachment_id") UUID attachment_id);

    @Query("SELECT a FROM Attachment a JOIN a.types t WHERE t.typeName = :typeName")
    Attachment findByTypeName(@Param("typeName") String typeName);

    @Transactional
    Page<Attachment> findByTypesTypeName(String typeName, Pageable pageable);

    @Transactional
    @Query("select a from Attachment  a where LOWER( a.fileName) like  %:keyword%")
    Page<Attachment> findByKeywordIgnoreCase(@Param("keyword") String keyword, Pageable pageable);

}