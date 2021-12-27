package com.jzh.lonershub.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jzh.lonershub.bean.Diary;
import com.jzh.lonershub.bean.Loner;
import com.jzh.lonershub.bean.LonerForm;
import com.jzh.lonershub.service.DiaryService;
import com.jzh.lonershub.service.LonerService;
import org.apache.catalina.connector.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.util.DecodeUtils;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Controller
public class LonerController {
    private LonerService lonerService;
    private DiaryService diaryService;

    @Autowired
    public void setLonerService(LonerService lonerService) {
        this.lonerService = lonerService;
    }

    @Autowired
    public void setDiaryService(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    @GetMapping(value = {"/index", "/"})
    public String index() {
        return "/index";
    }

    @GetMapping(value = "/login")
    public String toLogin(HttpSession session) {
        return "/login";
    }

    @GetMapping(value = "/register")
    public String toRegister(HttpSession session) {
        session.removeAttribute("errorMsg");
        return "/register";
    }

    /**
     * 注册用户
     * @param lonerForm 用户表单对象
     * @param br 表单填写验证错误值
     */
    @PostMapping(value = "/register")
    public String register(@Valid LonerForm lonerForm, BindingResult br, HttpSession session) throws FileNotFoundException, UnsupportedEncodingException {
        session.removeAttribute("errorMsg");
        // 处理头像上传并获取图片地址
        String lonerAvatarUrl = null;
        MultipartFile lonerAvatar = lonerForm.getLonerAvatar();
        if (lonerAvatar.getSize() > 0) {
            // 获取图片类型
            String type = lonerAvatar.getContentType();
            String myType;
            if (type != null) {
                myType = type.substring(type.indexOf('/') + 1);
            } else {
                session.setAttribute("errorMsg", "图片格式错误");
                return "/register";
            }
            // 获取上传地址
            File relativePath = new File(URLDecoder.decode(ResourceUtils.getURL("classpath:").getPath(), "utf-8"));
            File path = relativePath.getAbsoluteFile(); // 获取路径为： ${project}/target/classes/ 或者 ${jar包}/
            String fileName = System.currentTimeMillis() + "." + myType;
            File uploadPath = new File(path, "/static/avatar/" + fileName);
            if (!uploadPath.exists()) uploadPath.mkdirs();
            try {
                lonerAvatar.transferTo(uploadPath);
                lonerAvatarUrl = "/avatar/" + fileName;
            } catch (IOException e) {
                session.setAttribute("errorMsg","图片上传失败");
                e.printStackTrace();
                return "/register";
            }
        }

        // 判断表单是否填写有误
        if (br.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            for (ObjectError error : br.getAllErrors()) {
                errorMsg.append(error.getDefaultMessage()).append(" ");
            }
            session.setAttribute("errorMsg", errorMsg);
            return "/register";
        }

        // 判断邮箱是否已经被注册
        QueryWrapper<Loner> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("lonerEmail", lonerForm.getLonerEmail());
        Loner one = lonerService.getOne(queryWrapper);
        if (one != null) {
            session.setAttribute("errorMsg", "邮箱已经被注册");
            return "/register";
        }

        // 写入数据库
        Loner loner = new Loner();
        loner.setLonerName(lonerForm.getLonerName());
        loner.setLonerEmail(lonerForm.getLonerEmail());
        loner.setLonerPassword(lonerForm.getLonerPassword());
        loner.setLonerSignature(lonerForm.getLonerSignature());
        if (lonerAvatarUrl == null) lonerAvatarUrl = "/avatar/default.jpg";
        loner.setLonerAvatarUrl(lonerAvatarUrl);
        boolean save = lonerService.save(loner);
        if (save) {
            System.out.println(loner);
            session.setAttribute("successLoner", loner);
        } else {
            session.setAttribute("errorMsg", "注册失败，请重试");
            return "/register";
        }
        return "redirect:/success";
    }

    /**
     * 用户登录
     * @param lonerEmail 登录邮箱
     * @param lonerPassword 登录密码
     */
    @PostMapping(value = "/login")
    public String login(@RequestParam String lonerEmail,
                        @RequestParam String lonerPassword,
                        HttpSession session) {

        QueryWrapper<Loner> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("lonerEmail", lonerEmail);
        Loner loner = lonerService.getOne(queryWrapper);
        if (loner != null && loner.getLonerPassword().equals(lonerPassword)) {
            session.setAttribute("successLoner", loner);
            session.removeAttribute("msg");
            return "redirect:/success";
        } else {
            session.setAttribute("msg", "账号或密码错误");
            return "/login";
        }
    }
    @GetMapping(value = {"/success"})
    public String success(HttpSession session, Model model) {
        Loner successLoner = (Loner) session.getAttribute("successLoner");
        Integer lonerId = successLoner.getLonerId();
        QueryWrapper<Diary> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("creatorId", lonerId);
        List<Diary> diaryList = diaryService.list(queryWrapper);
        // 首先输出最新的日记
        diaryList.sort(new Comparator<Diary>() {
            @Override
            public int compare(Diary o1, Diary o2) {
                String t1 = o1.getCreateTime();
                String t2 = o2.getCreateTime();
                try {
                    Date d1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(t1);
                    Date d2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(t2);
                    return d2.compareTo(d1); // 如果d2大于d1则交换
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return -1;
            }
        });
        model.addAttribute("diaryList", diaryList);
        return "/success";
    }

    @PostMapping(value = "/update")
    public String update() {
        return null;
    }
}
