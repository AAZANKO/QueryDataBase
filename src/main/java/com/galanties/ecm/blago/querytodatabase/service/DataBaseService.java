package com.galanties.ecm.blago.querytodatabase.service;

import com.galanties.ecm.blago.querytodatabase.dao.model.DataBaseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface DataBaseService {

    boolean connectToDatabase(DataBaseEntity dataBaseEntity);

    boolean convertFileToFile(File file, MultipartFile[] files, DataBaseEntity dataBaseEntity);

    List<String> convertStringToStringService(String string, DataBaseEntity dataBaseEntity);
}