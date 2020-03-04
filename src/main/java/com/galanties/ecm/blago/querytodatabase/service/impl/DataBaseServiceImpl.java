package com.galanties.ecm.blago.querytodatabase.service.impl;

import com.galanties.ecm.blago.querytodatabase.dao.impl.DataBaseDaoImpl;
import com.galanties.ecm.blago.querytodatabase.dao.model.DataBaseEntity;
import com.galanties.ecm.blago.querytodatabase.service.DataBaseService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Service
public class DataBaseServiceImpl implements DataBaseService {

    @Autowired
    DataBaseDaoImpl dataBaseDaoImpl;

    @Override
    public boolean connectToDatabase(DataBaseEntity dataBaseEntity) {
        boolean result = dataBaseDaoImpl.connectToDataBase(dataBaseEntity);
        return result;
    }

    @Override
    public boolean convertFileToFile(File file, MultipartFile[] files, DataBaseEntity dataBaseEntity) {
        dataBaseDaoImpl.convertFileToFile(file, files, dataBaseEntity);
        return false;
    }

    @Override
    public List<String> convertStringToStringService(String string, DataBaseEntity dataBaseEntity) {
        List<String> list = new ArrayList<>();
        String stringResult = "";
//        String paramString = string.replaceAll("\\.00", ""); // ЕСЛИ НЕ ТОЛЬКО .00 !??!?!??!?!?!??!?
        String replaceTabulToComa = string.replace("\n", ",");
        String replaceKaretToComa = replaceTabulToComa.replace("\r", ",");
        String stringDualComaToComa = replaceKaretToComa.replace(",,", ",");
        String stringParams = stringDualComaToComa.replace(",,", ",");

        // если пробел и запятая в конце (без табуляции)
        Pattern pattern1 = Pattern.compile("^-?[0-9]{1,}\\.[0-9]{1,}E?[0-9]{1,} -?[0-9]{1,}\\.[0-9]{1,}E?[0-9]{1,},");
        // если запятая и запятая в конце (без табуляции)
        Pattern pattern2 = Pattern.compile("^-?[0-9]{1,}\\.[0-9]{1,}E?[0-9]{1,},-?[0-9]{1,}\\.[0-9]{1,}E?[0-9]{1,},");
        // если пробел и пробел (без табуляции)
        Pattern pattern6 = Pattern.compile("^-?[0-9]{1,}\\.[0-9]{1,}E?[0-9]{1,} -?[0-9]{1,}\\.[0-9]{1,}E?[0-9]{1,}\\s?");

        Matcher matcher1 = pattern1.matcher(stringParams);
        boolean b1 = matcher1.find();
        Matcher matcher2 = pattern2.matcher(stringParams);
        boolean b2 = matcher2.find();
        Matcher matcher6 = pattern6.matcher(stringParams);
        boolean b6 = matcher6.find();

        if (b1){
            String replaceComaToSpace = stringParams;
            list = dataBaseDaoImpl.convertStringToString(replaceComaToSpace, dataBaseEntity);
            return list;
        }else if (b2){
            String replaceComaToSpace = replaceComaToSpace(stringParams);
            list = dataBaseDaoImpl.convertStringToString(replaceComaToSpace, dataBaseEntity);
            return list;
        }else if (b6){
            String replaceComaToSpace = replaceSpaceToComa(stringParams);
            list = dataBaseDaoImpl.convertStringToString(replaceComaToSpace, dataBaseEntity);
            return list;
        }else {
            return list;
        }
    }


    /**
     *
     * @param string
     * @return
     */
    private String replaceComaToSpace(String string) {
        StringBuilder stringBuilder = new StringBuilder();
        char[] chars = string.toCharArray();
        int symbolNum = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == ',') {
                if (symbolNum % 2 != 0) {
                    stringBuilder.append(chars[i]);
                } else {
                    stringBuilder.append(' ');
                }
                symbolNum++;
            } else {
                stringBuilder.append(chars[i]);
            }
        }
        return new String(stringBuilder);
    }

    /**
     *
     * @param string
     * @return
     */
    private String replaceSpaceToComa(String string) {
        StringBuilder stringBuilder = new StringBuilder();
        char[] chars = string.toCharArray();
        int symbolNum = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == ' ') {
                if (symbolNum % 2 == 0) {
                    stringBuilder.append(chars[i]);
                } else {
                    stringBuilder.append(',');
                }
                symbolNum++;
            } else {
                stringBuilder.append(chars[i]);
            }
        }
        return new String(stringBuilder);
    }

}