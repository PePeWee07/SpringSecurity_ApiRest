package com.ucacue.UcaApp.service.apis.catia;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClient;

import org.springframework.beans.factory.annotation.Value;

public class CatiaService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final RestClient restclient;

    public CatiaService (
        @Value("${e}") String x,
        @Value("${e}") String z
    ) {
        this.restclient = RestClient.builder().baseUrl(x).build();
    }

    
}
