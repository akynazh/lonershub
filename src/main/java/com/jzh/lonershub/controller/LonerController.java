package com.jzh.lonershub.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jzh.lonershub.bean.Diary;
import com.jzh.lonershub.bean.Loner;
import com.jzh.lonershub.bean.LonerForm;
import com.jzh.lonershub.interceptor.VisitCountInterceptor;
import com.jzh.lonershub.service.DiaryService;
import com.jzh.lonershub.service.LonerService;
import com.jzh.lonershub.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;

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


    /**
     * @description: 添加提示登录消息
     * @author Jiang Zhihang
     * @date 2022/2/11 0:01
     */
    @GetMapping("/loginFirst")
    public String loginFirst(RedirectAttributesModelMap model) {
        model.addFlashAttribute("msg", "请先进行登录");
        return "redirect:/";
    }
    
    
    @GetMapping(value = {"/index", "/"})
    public String index(HttpSession session) {
        session.setAttribute("count", VisitCountInterceptor.visitCount);
        return "index";
    }

    @GetMapping(value = "/login")
    public String toLogin() {
        return "login";
    }

    @GetMapping(value = "/register")
    public String toRegister() {
        return "register";
    }

    /**
     * @description: 注册用户
     * @author Jiang Zhihang
     * @date 2021/12/28 14:55
     */
    @PostMapping(value = "/register")
    public String register(@Valid LonerForm lonerForm, BindingResult br,
                           HttpSession session, RedirectAttributesModelMap model) throws IOException {

        // 判断表单是否填写有误
        StringBuilder errors = new StringBuilder();
        if (br.hasErrors()) {
            for (ObjectError error : br.getAllErrors()) {
                errors.append(error.getDefaultMessage()).append("&");
            }
            model.addFlashAttribute("msg", errors);
            return "redirect:/";
        }

        // 判断邮箱是否已经被注册
        QueryWrapper<Loner> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("lonerEmail", lonerForm.getLonerEmail());
        Loner one = lonerService.getOne(queryWrapper);
        if (one != null) {
            model.addFlashAttribute("msg", "邮箱已被注册");
            return "redirect:/";
        }

        // 处理头像上传并获取图片地址
        String lonerAvatarUrl = null;
        MultipartFile lonerAvatar = lonerForm.getLonerAvatar();
        if (lonerAvatar.getSize() > 0) {
            // 获取图片类型
            String fileType = FileUtils.getFileType(lonerAvatar);
            if (fileType == null) {
                model.addFlashAttribute("msg", "图片类型错误");
                return "redirect:/";
            }

            // 设置文件名
            String fileName = System.currentTimeMillis() + "." + fileType;

            // 获取上传路径
            String uploadPath = FileUtils.getUploadPath("avatar", fileName);

            // 上传文件
            File uploadPathFile = new File(uploadPath);
            if (!uploadPathFile.exists()) uploadPathFile.mkdirs();
            try {
                lonerAvatar.transferTo(uploadPathFile);
                lonerAvatarUrl = "/avatar/" + fileName;
            } catch (IOException e) {
                model.addFlashAttribute("msg", "上传失败，请重试");
                return "redirect:/";
            }
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
            model.addFlashAttribute("msg", "注册成功");
            session.setAttribute("successLoner", loner);
        } else {
            model.addFlashAttribute("msg", "注册失败，请重试");
            return "redirect:/";
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
                        HttpSession session,
                        RedirectAttributesModelMap model) {

        QueryWrapper<Loner> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("lonerEmail", lonerEmail);
        Loner loner = lonerService.getOne(queryWrapper);
        if (loner != null && loner.getLonerPassword().equals(lonerPassword)) {
            session.setAttribute("successLoner", loner);
            return "redirect:/success";
        } else {
            model.addFlashAttribute("msg", "密码错误或用户不存在");
            return "redirect:/";
        }
    }

    /**
     * @description: 用户退出登录
     * @author Jiang Zhihang
     * @date 2021/12/28 14:56
     */
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributesModelMap model) {
        session.removeAttribute("successLoner");
        model.addFlashAttribute("msg", "退出成功");
        return "redirect:/";
    }

    /**
     * @description: 登录成功后显示成功页面
     * @author Jiang Zhihang
     * @date 2021/12/28 14:56
     */
    @GetMapping(value = {"/success"})
    public String success(@RequestParam(defaultValue = "1") Integer pn,
                          HttpSession session) {
        // 如果不是第一次加入到success页面且pn=1则直接返回无需多次操作数据库，在其他会修改diaryPage的地方修改对应session对象即可
        if (session.getAttribute("diaryPage") != null && pn == 1) return "success";
        // 获取当前Loner的ID
        Loner successLoner = (Loner) session.getAttribute("successLoner");
        Integer lonerId = successLoner.getLonerId();
        QueryWrapper<Diary> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("creatorId", lonerId);
        // 按日期降序排序
        queryWrapper.orderByDesc("createTime");

        Page<Diary> diaryPage = diaryService.page(new Page<>(pn, 4), queryWrapper);
        session.setAttribute("diaryPage", diaryPage);
        return "success";
    }

    /**
     * @description: 用户修改个人信息
     * @author Jiang Zhihang
     * @date 2021/12/28 14:56
     */
    @PostMapping(value = "/success/modify")
    public String modify(@Valid LonerForm lonerForm, BindingResult br,
                         HttpSession session, RedirectAttributesModelMap model) throws IOException, ServletException {
        Loner successLoner = (Loner) session.getAttribute("successLoner");
        UpdateWrapper<Loner> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("lonerId", successLoner.getLonerId());
        boolean flag = false; // 标记是否改变

        // 判断表单是否填写有误
        StringBuilder errors = new StringBuilder();
        if (br.hasErrors()) {
            for (ObjectError error : br.getAllErrors()) {
                errors.append(error.getDefaultMessage()).append("&");
            }
            model.addFlashAttribute("msg", errors);
            return "redirect:/success";
        }

        MultipartFile lonerAvatar = lonerForm.getLonerAvatar();
        // 如果修改头像
        if (lonerAvatar.getSize() > 0) {
            // 获取图片类型
            String fileType = FileUtils.getFileType(lonerAvatar);
            if (fileType == null) {
                model.addFlashAttribute("msg", "图像类型出错");
                return "redirect:/success";
            }

            // 设置文件名
            String fileName = System.currentTimeMillis() + "." + fileType;

            // 获取上传路径
            String uploadPath = FileUtils.getUploadPath("avatar", fileName);

            // 上传文件
            File uploadPathFile = new File(uploadPath);

            try {
                lonerAvatar.transferTo(uploadPathFile);
                String lonerAvatarUrl = "/avatar/" + fileName;
                updateWrapper.set("lonerAvatarUrl", lonerAvatarUrl);
                flag = true;
            } catch (IOException e) {
                model.addFlashAttribute("msg", "上传图片失败，请重试");
                return "redirect:/success";
            }
        }

        // 如果修改邮箱
        if (!successLoner.getLonerEmail().equals(lonerForm.getLonerEmail())) {
            // 判断邮箱是否已经被注册
            QueryWrapper<Loner> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("lonerEmail", lonerForm.getLonerEmail());
            Loner one = lonerService.getOne(queryWrapper);
            if (one != null) {
                model.addFlashAttribute("msg", "邮箱已被注册");
                return "redirect:/success";
            } else {
                updateWrapper.set("lonerEmail", lonerForm.getLonerEmail());
                flag = true;
            }
        }

        // 如果修改用户名
        if (!successLoner.getLonerName().equals(lonerForm.getLonerName())) {
            updateWrapper.set("lonerName", lonerForm.getLonerName());
            flag = true;
        }

        // 如果修改密码
        if (!successLoner.getLonerPassword().equals(lonerForm.getLonerPassword())) {
            updateWrapper.set("lonerPassword", lonerForm.getLonerPassword());
            flag = true;
        }

        // 如果修改个性签名
        if (!successLoner.getLonerSignature().equals(lonerForm.getLonerSignature())) {
            updateWrapper.set("lonerSignature", lonerForm.getLonerSignature());
            flag = true;
        }

        if (flag) {
            // 更新数据库对象
            boolean update = lonerService.update(updateWrapper);
            if (update) {
                Loner newLoner = lonerService.getOne(new QueryWrapper<Loner>().eq("lonerId", successLoner.getLonerId()));
                session.setAttribute("successLoner", newLoner);
                model.addFlashAttribute("msg", "保存成功");
            } else {
                model.addFlashAttribute("msg", "保存失败，请重试");
                return "redirect:/success";
            }
        }
        return "redirect:/success";
    }
}
