package com.jzh.lonershub.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.jzh.lonershub.bean.Loner;
import com.jzh.lonershub.bean.Participant;
import com.jzh.lonershub.bean.Video;
import com.jzh.lonershub.service.ParticipantService;
import com.jzh.lonershub.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class VideoController {
    private VideoService videoService;
    private ParticipantService participantService;

    @Autowired
    public void setVideoService(VideoService videoService) {
        this.videoService = videoService;
    }

    @Autowired
    public void setParticipantService(ParticipantService participantService) {
        this.participantService = participantService;
    }

    /**
     * @description: 上传video
     * @author Jiang Zhihang
     * @date 2021/12/30 23:43
     */
    @PostMapping("/success/upload")
    public String uploadVideo(@RequestParam String startTime, @RequestPart MultipartFile video,
                              @RequestParam String description, @RequestParam String videoName, HttpSession session,
                              HttpServletResponse response, HttpServletRequest request) throws IOException, ServletException {
        // 格式化开始时间
        startTime = startTime.replace("T", " ");
        startTime = startTime.concat(":00");
        String videoUrl = null;
        // 处理视频上传
        if (video.getSize() > 0) {
            // 获取video类型
            String type = video.getContentType();
            String myType;
            if (type != null) {
                myType = type.substring(type.indexOf('/') + 1);
            } else {
                response.addCookie(new Cookie("errorMsg", "video格式错误"));
                request.getRequestDispatcher("/success").forward(request, response);
                return null;
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
                request.getRequestDispatcher("/success").forward(request, response);
                return null;
            }
        } else {
            response.addCookie(new Cookie("errorMsg", "请选择video"));
            request.getRequestDispatcher("/success").forward(request, response);
            return null;
        }
        Video myVideo = new Video();
        myVideo.setVideoUrl(videoUrl);
        Loner successLoner = (Loner) session.getAttribute("successLoner");
        myVideo.setPublisherId(successLoner.getLonerId());
        myVideo.setDescription(description);
        myVideo.setStartTime(startTime);
        myVideo.setVideoName(videoName);
        myVideo.setParticipantsNum(1);
        Participant participant = new Participant();
        if (!videoService.save(myVideo)) {
            response.addCookie(new Cookie("errorMsg", "上传失败"));
            request.getRequestDispatcher("/success").forward(request, response);
            return null;
        }
        participant.setVideoId(myVideo.getVideoId());
        participant.setParticipantId(successLoner.getLonerId());
        participantService.save(participant);
        return "redirect:/success";
    }

    /**
     * @description: 初始化theater页面
     * @author Jiang Zhihang
     * @date 2021/12/30 23:43
     */
    @GetMapping(value = "/theater")
    public String theater(HttpSession session) {
        List<Video> list = videoService.list();
        List<Video> videoList = new ArrayList<>();
        // 只取出未来五天的预约影片
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 5);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String myTime = sdf.format(calendar.getTime());
        for (Video video : list) {
            if (video.getStartTime().compareTo(myTime) < 0) {
                videoList.add(video);
            }
        }
        session.setAttribute("videoList", videoList);
        return "theater";
    }

    /**
     * @description: 预约video
     * @author Jiang Zhihang
     * @date 2021/12/30 23:43
     */
    @GetMapping(value = "/theater/reserve")
    public String reserve(@RequestParam Integer videoId, HttpSession session,
                          HttpServletResponse response, HttpServletRequest request) throws ServletException, IOException {
        Loner successLoner = (Loner) session.getAttribute("successLoner");
        Integer lonerId = successLoner.getLonerId();
        QueryWrapper<Participant> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("participantId", lonerId);
        List<Participant> list = participantService.list(queryWrapper);
        for (Participant participant : list) {
            if (participant.getVideoId().equals(videoId)) {
                response.addCookie(new Cookie("errorMsg", "已经预约过咯"));
                request.getRequestDispatcher("/theater").forward(request, response);
                return null;
            }
        }
        Participant participant = new Participant();
        participant.setParticipantId(lonerId);
        participant.setVideoId(videoId);
        Video myVideo = videoService.getById(videoId);
        UpdateWrapper<Video> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("videoId", videoId);
        updateWrapper.set("participantsNum", myVideo.getParticipantsNum() + 1);

        // 开启事务！！！！！！！！！！！




        if (!participantService.save(participant) || !videoService.update(updateWrapper)) {
            response.addCookie(new Cookie("errorMsg", "预约失败"));
            request.getRequestDispatcher("/theater").forward(request, response);
            return null;
        }
        return "redirect:/theater";
    }

    /**
     * @description: 观看影片，加入影片页面
     * @author Jiang Zhihang
     * @date 2021/12/30 23:44
     */
    @GetMapping(value = "/theater/watch")
    public String watch(@RequestParam Integer videoId, HttpServletResponse response,
                        Model model, HttpServletRequest request) throws ParseException, IOException, ServletException {
        Video video = videoService.getById(videoId);
        if (video == null) {
            response.addCookie(new Cookie("errorMsg", "访问失败"));
            return "redirect:/theater";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date videoDate = sdf.parse(video.getStartTime());
        Date now = new Date();
        if (now.compareTo(videoDate) > 0) {
            model.addAttribute("video", video);
            return "watch";
        } else {
            response.addCookie(new Cookie("errorMsg", "该影片还未到播放时间"));
            request.getRequestDispatcher("/theater").forward(request, response);
            return null;
        }
    }

    @GetMapping(value = "/theater/delete")
    public String delete(@RequestParam Integer videoId, HttpServletResponse response,
                         HttpSession session, HttpServletRequest request) throws ServletException, IOException {
        Loner successLoner = (Loner) session.getAttribute("successLoner");
        if (successLoner.getLonerId() == 1) {
            if (videoService.removeById(videoId)) {
                response.addCookie(new Cookie("errorMsg", "删除失败"));
                request.getRequestDispatcher("/theater").forward(request, response);
                return null;
            }
        } else {
            response.addCookie(new Cookie("errorMsg", "没有权限"));
            request.getRequestDispatcher("/theater").forward(request, response);
            return null;
        }
        return "redirect:/theater";
    }
}
