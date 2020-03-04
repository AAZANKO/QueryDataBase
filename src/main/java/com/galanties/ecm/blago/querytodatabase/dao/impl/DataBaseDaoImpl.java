package com.galanties.ecm.blago.querytodatabase.dao.impl;

import com.galanties.ecm.blago.querytodatabase.dao.DataBaseDao;
import com.galanties.ecm.blago.querytodatabase.dao.model.DataBaseEntity;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@PropertySource("classpath:database.properties")
@Repository
public class DataBaseDaoImpl implements DataBaseDao {

    Connection connection;
    Statement statement;
    ResultSet resultSet;
    ResultSet resultSet2;

    @Override
    public boolean connectToDataBase(DataBaseEntity dataBaseEntity) {
        try {
            Class.forName("org.postgresql.Driver"); // Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(dataBaseEntity.getUrl(), dataBaseEntity.getLogin(), dataBaseEntity.getPassword());
            if (connection != null) {
                return true;
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            return false;
        }
    }


    @Override
    public boolean convertFileToFile(File file, MultipartFile[] files, DataBaseEntity dataBaseEntity) {
        try {
            connection = DriverManager.getConnection(dataBaseEntity.getUrl(), dataBaseEntity.getLogin(), dataBaseEntity.getPassword());
            statement = connection.createStatement();
            Path filepath;
            for (MultipartFile Multfile : files) {
                filepath = Paths.get(file.toString(), Multfile.getOriginalFilename());
                try (OutputStream os = Files.newOutputStream(filepath)) {
                    os.write(Multfile.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            File[] listFiles = file.listFiles();

            if (listFiles != null) {
                for (File Multfile : listFiles) {
                    try (BufferedReader br = new BufferedReader(new FileReader(Multfile))) {
                        String line;
                        String resultOfLine = "";
                        int booleanIncrement = 1;
                        while ((line = br.readLine()) != null) {
                            resultOfLine += line + "\n";
                        }
                        String halfResult = resultOfLine.replaceAll("\\.00", "");
                        String copyOfHalfResult = halfResult;
                        Pattern pattern = Pattern.compile("\\([0-9]{1,5},[0-9]{1,5}\\)");
                        Matcher matcher = pattern.matcher(halfResult);
                        while (matcher.find()) {
                            int start = matcher.start();
                            int end = matcher.end();
                            String coordinatesToConvert = halfResult.substring(start, end).replaceAll(",", " ").replaceAll("\\(", "").replaceAll("\\)", "");
                            resultSet = statement.executeQuery("select st_astext(st_transform(st_geomfromtext(st_astext(st_affine(st_geomfromtext('POINT(" + coordinatesToConvert + ")')," + dataBaseEntity.getEther() + "))," + dataBaseEntity.getSrid() + "),3857));");
                            while (resultSet.next()) {
                                String queryResult = resultSet.getString("st_astext").replaceAll("POINT\\(", "").replaceAll(" ", ",").replaceAll("\\)", "");
                                if (booleanIncrement % 2 != 0) {
                                    String roundingQueryResult = metchodRoundingValues(queryResult);
                                    copyOfHalfResult = copyOfHalfResult.replaceAll(halfResult.substring(start, end), roundingQueryResult);
                                }
                                booleanIncrement++;
                            }
                        }
                        String pathToFile = Multfile.getAbsolutePath().replaceAll("fotoplan", "fotoplanUpdate");
                        try (FileWriter fileWriter = new FileWriter(new File(pathToFile));
                             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                            bufferedWriter.write(copyOfHalfResult);
                        } catch (IOException ex1) {
                            ex1.printStackTrace();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<String> convertStringToString(String string, DataBaseEntity dataBaseEntity) {
        String queryResult = "";
        String queryResult2 = "";
        List<String> list = new ArrayList<>();
        try {
            connection = DriverManager.getConnection(dataBaseEntity.getUrl(), dataBaseEntity.getLogin(), dataBaseEntity.getPassword());
            statement = connection.createStatement();
            String[] spaceArray = string.split(" ");
            String[] comaArray = string.split(",");

            if (spaceArray.length == 2 & comaArray.length == 1) {
                resultSet = statement.executeQuery("select st_astext(st_transform(st_geomfromtext(st_astext(st_affine(st_geomfromtext('POINT(" + string + ")')," + dataBaseEntity.getEther() + "))," + dataBaseEntity.getSrid() + "),3857));");
                while (this.resultSet.next()) {
                    queryResult = this.resultSet.getString("st_astext").replaceAll("POINT\\(", "").replaceAll("\\)", "");
                }
                list.add(queryResult);
                resultSet2 = statement.executeQuery("select st_astext(st_geomfromtext(st_astext(st_affine(st_geomfromtext('POINT(" + string + ")')," + dataBaseEntity.getEther() + "))," + dataBaseEntity.getSrid() + "));");
                while (this.resultSet2.next()) {
                    queryResult2 = this.resultSet2.getString("st_astext").replaceAll("POINT\\(", "").replaceAll("\\)", "");
                }
                list.add(queryResult2);
            } else {
                resultSet = statement.executeQuery("select st_astext(st_transform(st_geomfromtext(st_astext(st_affine(st_geomfromtext('LINESTRING(" + string + ")')," + dataBaseEntity.getEther() + "))," + dataBaseEntity.getSrid() + "),3857));");
                while (resultSet.next()) {
                    queryResult = resultSet.getString("st_astext").replaceAll("LINESTRING\\(", "").replaceAll("\\)", "");
                }
                list.add(queryResult);
                resultSet2 = statement.executeQuery("select st_astext(st_geomfromtext(st_astext(st_affine(st_geomfromtext('LINESTRING(" + string + ")')," + dataBaseEntity.getEther() + "))," + dataBaseEntity.getSrid() + "));");
                while (this.resultSet2.next()) {
                    queryResult2 = this.resultSet2.getString("st_astext").replaceAll("LINESTRING\\(", "").replaceAll("\\)", "");
                }
                list.add(queryResult2);
            }
            /*if (spaceArray.length == 2 & comaArray.length == 1) {
                resultSet = statement.executeQuery("select st_astext(st_transform(st_geomfromtext(st_astext(st_affine(st_geomfromtext('POINT(" + string + ")')," + dataBaseEntity.getEther() + "))," + dataBaseEntity.getSrid() + "),3857));");
                while (resultSet.next()) {
                    queryResult = resultSet.getString("st_astext").replaceAll("POINT\\(", "").replaceAll("\\)", "");
                }
            } else if (spaceArray.length == 3 & comaArray.length == 2) {
                resultSet = statement.executeQuery("select st_astext(st_transform(st_geomfromtext(st_astext(st_affine(st_geomfromtext('LINESTRING(" + string + ")')," + dataBaseEntity.getEther() + "))," + dataBaseEntity.getSrid() + "),3857));");
                while (resultSet.next()) {
                    queryResult = resultSet.getString("st_astext").replaceAll("LINESTRING\\(", "").replaceAll("\\)", "");
                }
            } else {
                if (spaceArray.length - comaArray.length == 1) {
                    resultSet = statement.executeQuery("select st_astext(st_transform(st_geomfromtext(st_astext(st_affine(st_geomfromtext('POLYGON((" + string + "))')," + dataBaseEntity.getEther() + "))," + dataBaseEntity.getSrid() + "),3857));");
                    while (resultSet.next()) {
                        queryResult = resultSet.getString("st_astext").replaceAll("POLYGON\\(\\(", "").replaceAll("\\)\\)", "");
                    }
                } else {
                    return "";
                }
            }*/
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private String metchodRoundingValues(String string) {
        String[] doubleValueInString = string.split(",");
        int booleanIncrement = 1;
        StringBuilder resultStringBuilder = new StringBuilder();
        for (String s : doubleValueInString) {
            double doubleValue = Double.parseDouble(s);
            int knockFractionalParts = (int) doubleValue;
            String ofNumberToString = String.valueOf(knockFractionalParts);
            char[] arrayChars = ofNumberToString.toCharArray();
            int roundingValue = Character.getNumericValue(arrayChars[arrayChars.length - 1]);
            int resultRoundingValue;
            if (roundingValue >= 5) {
                resultRoundingValue = Character.getNumericValue(arrayChars[arrayChars.length - 2]) + 1;
            } else {
                resultRoundingValue = Character.getNumericValue(arrayChars[arrayChars.length - 2]);
            }
            String fromCharsToString = String.valueOf(resultRoundingValue);
            arrayChars[arrayChars.length - 2] = fromCharsToString.charAt(0);
            arrayChars[arrayChars.length - 1] = '0';
            String resultString = new String(arrayChars);

            resultStringBuilder.append(resultString);
            resultStringBuilder.append(".00");

            if (booleanIncrement % 2 != 0) {
                resultStringBuilder.append(",");
            }
            booleanIncrement++;
        }
        return new String(resultStringBuilder);
    }

}