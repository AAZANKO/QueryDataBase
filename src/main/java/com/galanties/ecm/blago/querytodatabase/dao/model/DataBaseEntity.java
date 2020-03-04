package com.galanties.ecm.blago.querytodatabase.dao.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Component
public class DataBaseEntity {

    @Value("${project.config.url}")
    private String url;
    @Value("${project.config.login}")
    private String login;
    @Value("${project.config.password}")
    private String password;
    @Value("${project.config.ether}")
    private String ether;
    @Value("${project.config.srid}")
    private String srid;
}