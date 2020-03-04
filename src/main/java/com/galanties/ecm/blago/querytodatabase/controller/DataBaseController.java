package com.galanties.ecm.blago.querytodatabase.controller;

import com.galanties.ecm.blago.querytodatabase.dao.model.DataBaseEntity;
import com.galanties.ecm.blago.querytodatabase.service.impl.DataBaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@SessionAttributes("todos")
public class DataBaseController {

    @Autowired
    private DataBaseServiceImpl dataBaseService;
    @Autowired
    private DataBaseEntity dataBaseEntity;

    @GetMapping("/")
    public String HelloControllerDemo() {
        return "redirect:/ConnectToDatabase";
    }

    @GetMapping("/HelloPage")
    public String HomePageController() {
        return "redirect:/ConnectToDatabase";
    }

    @GetMapping("/ConnectToDatabase")
    public String ConnectToDatabaseController(Model model) {
        String url = dataBaseEntity.getUrl();
        String login = dataBaseEntity.getLogin();
        String password = dataBaseEntity.getPassword();
        String ether = dataBaseEntity.getEther();
        String srid = dataBaseEntity.getSrid();

        model.addAttribute("url", url);
        model.addAttribute("login", login);
        model.addAttribute("password", password);
        model.addAttribute("ether", ether);
        model.addAttribute("srid", srid);
//        model.addAttribute("configur", dataBaseEntity);
        return "ConnectToDatabase"; // redirect
    }

    @PostMapping("/ConnectToDatabase")
    public String ConnectToDatabaseController(@RequestParam("url") String url,
                                              @RequestParam("login") String login,
                                              @RequestParam("password") String password,
                                              @RequestParam("ether") String ether,
                                              @RequestParam("srid") String srid,
                                              Model model) {
        if ("".equals(url.trim()) | "".equals(login.trim()) | "".equals(password.trim()) | "".equals(ether.trim()) | "".equals(srid.trim())) {
            model.addAttribute("message", "Одно из полей не заполненно...!!!");
            model.addAttribute("url", url);
            model.addAttribute("login", login);
            model.addAttribute("password", password);
            model.addAttribute("ether", ether);
            model.addAttribute("srid", srid);
            return "ConnectToDatabase";
        } else {
            DataBaseEntity dataBaseEntity = new DataBaseEntity(url, login, password, ether, srid);
            boolean resultConnect = dataBaseService.connectToDatabase(dataBaseEntity);
            if (resultConnect == false) {
                model.addAttribute("url", url);
                model.addAttribute("login", login);
                model.addAttribute("password", password);
                model.addAttribute("ether", ether);
                model.addAttribute("srid", srid);
                model.addAttribute("message", "Не удалось подключиться к базе данных, проверьте: URL, Login, Password");
                return "ConnectToDatabase";
            }
            model.addAttribute("todos", dataBaseEntity); // положил в сессию "todos"
            model.addAttribute("dataBaseEntity", resultConnect);
//            return "OpenFile"; //  если удачно зарегистрировался...
            return "redirect:/UploadFile";
        }
    }


    @GetMapping("/UploadFile")
    public String getSubmit() {
//        return "ConnectToDatabase";
        return "OpenFile";
    }

    @PostMapping("/UploadFile")
    public String postSubmit(@RequestParam("files") MultipartFile[] files, ModelMap modelMap, @SessionAttribute("todos") DataBaseEntity dataBaseEntity, Model model) {
        modelMap.addAttribute("files", files);
        if (!files[0].isEmpty()) {
            boolean validateNameFiles = true;
            for (int i = 0; i < files.length; i++) {
                String originalFilename = files[i].getOriginalFilename();
                String substring = originalFilename.trim().substring(originalFilename.trim().length() - 3, originalFilename.trim().length());
                if (!"tab".equals(substring.toLowerCase())) {
                    validateNameFiles = false;
                }
            }
            if (validateNameFiles) {
                Path pathNewFiles = Paths.get("result", "tab");
                File file = pathNewFiles.toFile();
                file.mkdirs();
                dataBaseService.convertFileToFile(file, files, dataBaseEntity);
            } else {
                return "ExeptionValidateNameFiles";
            }
            model.addAttribute("messageTrue", "Файлы сконвертированы, в 'QueryDataBase/result/tab'");
            return "OpenFile";
        } else {
            model.addAttribute("message", "Вы не выбрали файлы...!!!");
            return "OpenFile";
        }
    }

    @GetMapping("/ConvertString")
    public String getConvertString() {
        return "StringConvertation";
    }

    @PostMapping("/ConvertString")
    public String getConvertStringRequest(@RequestParam("coordinates") String coordinates, @SessionAttribute("todos") DataBaseEntity dataBaseEntity, Model model) {
        if (!coordinates.equals("")) {
            List<String> list;
            list = dataBaseService.convertStringToStringService(coordinates, dataBaseEntity);
            if (list.isEmpty()) {
                model.addAttribute("message", "Вы ввели неверные координаты...!!!");
                return "StringConvertation";
            }
            model.addAttribute("coordinate1", list.get(0));
            model.addAttribute("coordinate2", list.get(1));
            model.addAttribute("srid", "Результат: SRID " + dataBaseEntity.getSrid());
            return "StringConvertation";
        } else {
            model.addAttribute("messageNull", "Вы не ввели координаты...!!!");
            return "StringConvertation";
        }
    }

}