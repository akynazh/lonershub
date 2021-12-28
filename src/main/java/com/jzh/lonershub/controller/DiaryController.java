package com.jzh.lonershub.controller;

import com.jzh.lonershub.bean.Diary;
import com.jzh.lonershub.bean.Loner;
import com.jzh.lonershub.service.DiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @description: 日记控制器，包括日记编写，上传及展示等功能
 * @author Jiang Zhihang
 * @date 2021/12/28 14:54
 */
@Controller
public class DiaryController {
    private DiaryService diaryService;

    @Autowired
    public void setDiaryService(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    @PostMapping(value = "/success/write")
    public String write(@RequestParam String content, HttpSession session) {
        if (!StringUtils.hasText(content)) {
            content = "无话可说...";
        }
        String creatTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Diary diary = new Diary();
        Loner successLoner = (Loner) session.getAttribute("successLoner");
        Integer creatorId = successLoner.getLonerId();
        diary.setCreatorId(creatorId);
        diary.setCreateTime(creatTime);
        diary.setContent(content);
        if (!diaryService.save(diary)) {
            session.setAttribute("errorMsg", "保存失败");
        }
        return "redirect:/success";
    }
}
