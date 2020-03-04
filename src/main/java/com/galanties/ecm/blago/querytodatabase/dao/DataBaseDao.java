package com.galanties.ecm.blago.querytodatabase.dao;

import com.galanties.ecm.blago.querytodatabase.dao.model.DataBaseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface DataBaseDao {

    boolean connectToDataBase(DataBaseEntity dataBaseEntity);

    boolean convertFileToFile(File file, MultipartFile[] files, DataBaseEntity dataBaseEntity);

    List<String> convertStringToString(String string, DataBaseEntity dataBaseEntity);
}
