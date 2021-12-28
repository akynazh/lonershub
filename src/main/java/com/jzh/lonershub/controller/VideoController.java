package com.jzh.lonershub.controller;

import com.jzh.lonershub.bean.Loner;
import com.jzh.lonershub.bean.Video;
import com.jzh.lonershub.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Controller
public class VideoController {
    private VideoService videoService;

    @Autowired
    public void setVideoService(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping("/theater")
    public String theater() {
        return "/theater";
    }

    @PostMapping("/success/upload")
    public String uploadVideo(@RequestParam String startTime, @RequestPart MultipartFile video,
                              @RequestParam String description, HttpSession session,
                              HttpServletResponse response) throws IOException {
        startTime = startTime.replace("T", " ");
        startTime = startTime.concat(":00");
        String videoUrl = null;
        if (video.getSize() > 0) {
            // 获取video类型
            String type = video.getContentType();
            String myType;
            if (type != null) {
                myType = type.substring(type.indexOf('/') + 1);
            } else {
                response.addCookie(new Cookie("errorMsg", "video格式错误"));
                return "redirect:/success";
            }
            // 获取上传地址
            File relativePath = new File(URLDecoder.decode(ResourceUtils.getURL("classpath:").getPath(), "utf-8"));
            File path = relativePath.getAbsoluteFile(); // 获取路径为： ${project}/target/classes/ 或者 ${jar包}/
            String fileName = System.currentTimeMillis() + "." + myType;
            File uploadPath = new File(path, "/static/video/" + fileName);
            if (!uploadPath.exists()) uploadPath.mkdirs();
            try {
                video.transferTo(uploadPath);
                videoUrl = "/video/" + fileName;
            } catch (IOException e) {
                response.addCookie(new Cookie("errorMsg", "video上传失败"));
                e.printStackTrace();
                return "redirect:/success";
            }
        } else {
            response.addCookie(new Cookie("errorMsg", "请选择video"));
            return "redirect:/success";
        }
        Video myVideo = new Video();
        myVideo.setVideoUrl(videoUrl);
        Loner successLoner = (Loner) session.getAttribute("successLoner");
        myVideo.setPublisherId(successLoner.getLonerId());
        myVideo.setDescription(description);
        myVideo.setStartTime(startTime);
        if (!videoService.save(myVideo)) {
            response.addCookie(new Cookie("errorMsg", "上传失败"));
            return "redirect:/success";
        }
        return "redirect:/success";
    }
}
