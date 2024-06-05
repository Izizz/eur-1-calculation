package com.example.eurcertificatecalc.calculation.util;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

import java.util.TreeMap;


public class CountriesUtil {

    public static Map<String,String> getCountriesCodes(){

        Map<String,String> codeMap = new TreeMap<>();

        Locale.setDefault(new Locale("uk", "UA"));

        String[] countryCodes = Locale.getISOCountries();

        Arrays.stream(countryCodes).toList().forEach(countryCode -> {
            Locale locale = new Locale("", countryCode);
            codeMap.put(locale.getDisplayCountry(new Locale("uk", "UA")).toUpperCase(),countryCode);
        });

        return codeMap;
    }

}