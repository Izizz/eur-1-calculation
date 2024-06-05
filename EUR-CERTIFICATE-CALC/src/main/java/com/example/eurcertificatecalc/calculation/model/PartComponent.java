package com.example.eurcertificatecalc.calculation.model;


import com.example.eurcertificatecalc.calculation.exception.MissingFieldException;
import com.example.eurcertificatecalc.calculation.util.CountriesUtil;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Builder
@Data
@Slf4j
public class PartComponent {

    private Long id;
    private String description;
    private String productArticle;
    private String provider;
    private Long customsIdForPart;
    private DocumentOfOrigin documentOfOrigin;
    private String countryOrigin;
    private Double quantity;
    private String typeOfCounting;
    private Double valueForPiece;
    private Double price;
    private String countryCode;
    private String declarationNumber;
    private String invoiceNumber;
    private Date invoiceDate;
    private Integer numberOfGroupInCustomsDeclaration;
    private Double percentage;

    public static PartComponent instance(Map<String, Object> data) throws ParseException {
        PartComponent part = PartComponent.builder()
                .id((Double.valueOf(data.get("№").toString()).longValue()))
                .description(String.valueOf(data.get("Найменування комплектуючих")))
                .productArticle(data.get("Артикул").toString())
                .provider(String.valueOf(data.get("Постачальник")))
                .customsIdForPart(Long.parseLong(String.valueOf(data.get("Код товару УКТЗЕД")).replaceAll(" +", "")))
                .countryOrigin(String.valueOf(data.get("Країна походження")))
                .countryCode(getCodeForCountry(String.valueOf(data.get("Країна походження"))))
                .quantity((Double.valueOf(String.valueOf(data.get("Кількість")))))
                .typeOfCounting((String) data.get("Од. Виміру"))
                .valueForPiece((Double) data.get("Ціна"))
                .price((Double) data.get("Сума"))
                .build();

        var document = data.get("Документ Надходження");
        if (!document.toString().isEmpty()) {
            part.setDocumentOfOrigin(DocumentOfOrigin.builder()
                    .id(UUID.randomUUID())
                    .numberOfDocument(Long.parseLong(data.get("Документ Надходження").toString().substring(14, 25)))
                    .date(new SimpleDateFormat("dd.MM.yyyy").parse(data.get("Документ Надходження").toString().substring(30, 40)))
                    .build());
        }
        return part;
    }

    private static String getCodeForCountry(String country) {
        return CountriesUtil.getCountriesCodes().get(country);
    }

    public void getMissingFields(List<String> fieldNames) throws NoSuchFieldException, IllegalAccessException {
        Class<?> clazz = this.getClass();
        for (String fieldName : fieldNames) {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(this);
            if (String.valueOf(value).isEmpty())
                throw new MissingFieldException("Field '" + fieldName + "' is null for object " + description);

        }
    }
}
