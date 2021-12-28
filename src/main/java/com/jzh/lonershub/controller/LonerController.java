package com.jzh.lonershub.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

/**
 * @description: 用户控制器，包括登录，注册，修改信息等功能
 * @author Jiang Zhihang
 * @date 2021/12/28 14:54
 */
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
     * @description: 注册用户
     * @author Jiang Zhihang
     * @date 2021/12/28 14:55
     */
    @PostMapping(value = "/register")
    public String register(@Valid LonerForm lonerForm, BindingResult br, HttpSession session) throws FileNotFoundException, UnsupportedEncodingException {
        session.removeAttribute("errorMsg");

        // 判断表单是否填写有误
        if (br.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            for (ObjectError error : br.getAllErrors()) {
                errorMsg.append(error.getDefaultMessage()).append(" ");
            }
            session.setAttribute("errorMsg", errorMsg);
            return "/register";
        }

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
     * @description: 用户登录
     * @author Jiang Zhihang
     * @date 2021/12/28 14:55
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

    /**
     * @description: 用户退出登录
     * @author Jiang Zhihang
     * @date 2021/12/28 14:56
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "/index";
    }

    /**
     * @description: 登录成功后显示成功页面
     * @author Jiang Zhihang
     * @date 2021/12/28 14:56
     */
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



    /**
     * @description: 用户修改个人信息
     * @author Jiang Zhihang
     * @date 2021/12/28 14:56
     */
    @PostMapping(value = "/success/modify")
    public String modify(@Valid LonerForm lonerForm, BindingResult br, HttpSession session) throws FileNotFoundException, UnsupportedEncodingException {
        session.removeAttribute("errorMsg");
        Loner successLoner = (Loner) session.getAttribute("successLoner");
        UpdateWrapper<Loner> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("lonerId", successLoner.getLonerId());

        // 判断表单是否填写有误
        if (br.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            for (ObjectError error : br.getAllErrors()) {
                errorMsg.append(error.getDefaultMessage()).append(" ");
            }
            session.setAttribute("errorMsg", errorMsg);
            return "/success";
        }

        MultipartFile lonerAvatar = lonerForm.getLonerAvatar();
        // 如果修改头像
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
                String lonerAvatarUrl = "/avatar/" + fileName;
                updateWrapper.set("lonerAvatarUrl", lonerAvatarUrl);
            } catch (IOException e) {
                session.setAttribute("errorMsg","图片上传失败");
                e.printStackTrace();
                return "/success";
            }
        }

        // 如果修改邮箱
        if (!successLoner.getLonerEmail().equals(lonerForm.getLonerEmail())) {
            // 判断邮箱是否已经被注册
            QueryWrapper<Loner> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("lonerEmail", lonerForm.getLonerEmail());
            Loner one = lonerService.getOne(queryWrapper);
            if (one != null) {
                session.setAttribute("errorMsg", "邮箱已经被注册");
                return "/success";
            } else {
                updateWrapper.set("lonerEmail", lonerForm.getLonerEmail());
            }
        }

        // 如果修改用户名
        if (!successLoner.getLonerName().equals(lonerForm.getLonerName())) {
            updateWrapper.set("lonerName", lonerForm.getLonerName());
        }

        // 如果修改密码
        if (!successLoner.getLonerPassword().equals(lonerForm.getLonerPassword())) {
            updateWrapper.set("lonerPassword", lonerForm.getLonerPassword());
        }

        // 如果修改个性签名
        if (!successLoner.getLonerSignature().equals(lonerForm.getLonerSignature())) {
            updateWrapper.set("lonerSignature", lonerForm.getLonerSignature());
        }

        // 更新数据库对象
        boolean update = lonerService.update(updateWrapper);
        if (update) {
            Loner newLoner = lonerService.getOne(new QueryWrapper<Loner>().eq("lonerId", successLoner.getLonerId()));
            session.setAttribute("successLoner", newLoner);
        } else {
            session.setAttribute("errorMsg", "修改失败，请重试");
            return "/success";
        }
        return "redirect:/success";
    }
}
