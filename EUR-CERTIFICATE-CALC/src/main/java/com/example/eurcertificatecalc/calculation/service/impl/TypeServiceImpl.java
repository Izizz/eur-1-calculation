package com.example.eurcertificatecalc.calculation.service.impl;

import com.example.eurcertificatecalc.calculation.data.entity.Type;
import com.example.eurcertificatecalc.calculation.data.repository.TypeRepository;
import com.example.eurcertificatecalc.calculation.service.TypeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TypeServiceImpl implements TypeService {

    private final TypeRepository typeRepository;

    @Override
    public Type getType(String typeName) {
        return typeRepository.getTypeByTypeName(typeName)
                .orElseThrow(() -> new EntityNotFoundException("Type was not found in database :" + typeName));
    }
}
