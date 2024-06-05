package com.example.eurcertificatecalc.calculation.data.repository;

import com.example.eurcertificatecalc.calculation.data.entity.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TypeRepository extends JpaRepository<Type, Long> {

    Optional<Type> getTypeByTypeName(String typeName);
}