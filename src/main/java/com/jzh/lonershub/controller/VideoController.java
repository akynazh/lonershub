package com.jzh.lonershub.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.jzh.lonershub.bean.Loner;
import com.jzh.lonershub.bean.Message;
import com.jzh.lonershub.bean.Participant;
import com.jzh.lonershub.bean.Video;
import com.jzh.lonershub.service.MessageService;
import com.jzh.lonershub.service.ParticipantService;
import com.jzh.lonershub.service.VideoService;
import com.jzh.lonershub.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class VideoController {
    private VideoService videoService;
    private ParticipantService participantService;
    private MessageService messageService;

    @Autowired
    public void setVideoService(VideoService videoService) {
        this.videoService = videoService;
    }

    @Autowired
    public void setParticipantService(ParticipantService participantService) {
        this.participantService = participantService;
    }

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * @description: 上传video
     * @author Jiang Zhihang
     * @date 2021/12/30 23:43
     */
    @PostMapping("/success/upload")
    @Transactional
    public String uploadVideo(@RequestParam String startTime, @RequestPart MultipartFile video,
                              @RequestParam String description, @RequestParam String videoName, HttpSession session,
                              RedirectAttributesModelMap model) throws IOException {
        // 格式化开始时间
        startTime = startTime.replace("T", " ");
        startTime = startTime.concat(":00");
        String videoUrl = null;

        // 处理视频上传
        if (video.getSize() > 0) {
            // 获取video类型
            String fileType = FileUtils.getFileType(video);
            if (fileType == null) {
                model.addFlashAttribute("msg", "文件类型出错");
                return "redirect:/success";
            }

            // 设置文件名
            String fileName = System.currentTimeMillis() + "." + fileType;

            // 图片路径
            videoUrl = "/video/" + fileName;

            // 获取上传路径
            String uploadPath = FileUtils.getUploadPath("video", fileName);
            File uploadPathFile = new File(uploadPath);
            if (!uploadPathFile.exists()) uploadPathFile.mkdirs();

            // 创建video对象
            Video myVideo = new Video();
            myVideo.setVideoUrl(videoUrl);
            Loner successLoner = (Loner) session.getAttribute("successLoner");
            myVideo.setPublisherId(successLoner.getLonerId());
            myVideo.setDescription(description);
            myVideo.setStartTime(startTime);
            myVideo.setVideoName(videoName);
            myVideo.setParticipantsNum(1);

            // 处理上传业务
            try {
                video.transferTo(uploadPathFile);
                videoService.save(myVideo);
                Participant participant = new Participant();
                participant.setVideoId(myVideo.getVideoId());
                participant.setParticipantId(successLoner.getLonerId());
                participantService.save(participant);
            } catch (Exception e) {
                model.addFlashAttribute("msg", "上传失败，请重试");
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return "redirect:/success";
            }
        } else {
            model.addFlashAttribute("msg", "请选择video");
            return "redirect:/success";
        }
        model.addFlashAttribute("msg", "上传成功");
        return "redirect:/success";
    }

    /**
     * @description: 初始化theater页面
     * @author Jiang Zhihang
     * @date 2021/12/30 23:43
     */
    @GetMapping(value = "/theater")
    public String theater(HttpSession session, RedirectAttributesModelMap model) {
        List<Video> list = videoService.list();
        List<Video> videoList = new ArrayList<>();
        // 只取出未来五天和过去五天的预约影片
        Calendar calendar = Calendar.getInstance();
        Calendar calendar1 = Calendar.getInstance();
        calendar.add(Calendar.DATE, 5);
        calendar1.add(Calendar.DATE, -5);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String myTime = sdf.format(calendar.getTime());
        String myTime1 = sdf.format(calendar1.getTime());
        for (Video video : list) {
            if (video.getStartTime().compareTo(myTime) < 0 && video.getStartTime().compareTo(myTime1) > 0) {
                videoList.add(video);
            }
        }
        session.setAttribute("videoList", videoList);
        model.addFlashAttribute("msg", "上传成功");
        return "theater";
    }

    /**
     * @description: 预约video
     * @author Jiang Zhihang
     * @date 2021/12/30 23:43
     */
    @GetMapping(value = "/theater/reserve")
    @Transactional
    public String reserve(@RequestParam Integer videoId, HttpSession session,
                          RedirectAttributesModelMap model) throws ServletException, IOException {
        Loner successLoner = (Loner) session.getAttribute("successLoner");
        Integer lonerId = successLoner.getLonerId();
        QueryWrapper<Participant> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("participantId", lonerId);
        List<Participant> list = participantService.list(queryWrapper);
        for (Participant participant : list) {
            if (participant.getVideoId().equals(videoId)) {
                model.addFlashAttribute("msg", "预约过了");
                return "redirect:/theater";
            }
        }
        Participant participant = new Participant();
        participant.setParticipantId(lonerId);
        participant.setVideoId(videoId);
        Video myVideo = videoService.getById(videoId);
        UpdateWrapper<Video> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("videoId", videoId);
        updateWrapper.set("participantsNum", myVideo.getParticipantsNum() + 1);

        try {
            participantService.save(participant);
            videoService.update(updateWrapper);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            model.addFlashAttribute("msg", "预约失败，请重试");
            return "redirect:/theater";
        }
        return "redirect:/theater";
    }

    /**
     * @description: 观看影片，加入影片页面
     * @author Jiang Zhihang
     * @date 2021/12/30 23:44
     */
    @GetMapping(value = "/theater/watch")
    public String watch(@RequestParam Integer videoId, RedirectAttributesModelMap model) throws ParseException, IOException, ServletException {
        Video video = videoService.getById(videoId);
        if (video == null) {
            model.addFlashAttribute("msg", "video过不存在");
            return "redirect:/theater";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date videoDate = sdf.parse(video.getStartTime());
        Date now = new Date();
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("videoId", videoId);
        List<Message> originMessageList = messageService.list(queryWrapper);
        List<Message> messageList = originMessageList;
        List<Message> moreMessageList = new ArrayList<>();
        if (originMessageList.size() > 10) {
            messageList = originMessageList.subList(0, 10);
            moreMessageList = originMessageList.subList(10, originMessageList.size());
        }
        if (now.compareTo(videoDate) > 0) {
            model.addFlashAttribute("video", video);
            model.addFlashAttribute("messageList", messageList);
            model.addFlashAttribute("moreMessageList", moreMessageList);
            return "watch";
        } else {
            model.addFlashAttribute("msg", "未到播放时间");
            return "redirect:/theater";
        }
    }

    @GetMapping(value = "/theater/delete")
    @Transactional
    public String delete(@RequestParam Integer videoId, RedirectAttributesModelMap model,
                         HttpSession session) {
        Loner successLoner = (Loner) session.getAttribute("successLoner");
        QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("publisherId", videoId);
        Video one = videoService.getOne(queryWrapper);
        if (successLoner.getLonerId() == 1 || one.getPublisherId().equals(successLoner.getLonerId())) {
            try {
                QueryWrapper<Participant> participantQueryWrapper = new QueryWrapper<>();
                QueryWrapper<Message> messageQueryWrapper = new QueryWrapper<>();
                participantQueryWrapper.eq("videoId", videoId);
                messageQueryWrapper.eq("videoId", videoId);
                participantService.remove(participantQueryWrapper);
                messageService.remove(messageQueryWrapper);
                videoService.removeById(videoId);
            } catch (Exception e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                model.addFlashAttribute("msg", "删除失败，请重试");
                return "redirect:/theater";
            }
        } else {
            model.addFlashAttribute("msg", "没有权限，请联系管理员");
            return "redirect:/theater";
        }
        model.addFlashAttribute("msg", "删除成功");
        return "redirect:/theater";
    }

    @PostMapping(value = "/theater/watch/comment")
    @ResponseBody
    public Map<String, String> comment(HttpSession session, @RequestBody Map<String, String> jsonData) {
        // @PathVariable 和 @RequestParam 只支持get请求
        Loner loner = (Loner)session.getAttribute("successLoner");
        Message message = new Message();
        String content = jsonData.get("content");
        Integer videoId = Integer.valueOf(jsonData.get("videoId"));
        message.setCreatorId(loner.getLonerId());
        message.setVideoId(videoId);
        message.setContent(content);
        message.setCreatorName(loner.getLonerName());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createTime = sdf.format(new Date());
        message.setCreateTime(createTime);
        Map<String, String> myMsg = new HashMap<>();
        myMsg.put("name", loner.getLonerName());
        myMsg.put("time", message.getCreateTime());
        if (!messageService.save(message)) {
            myMsg.put("success", "0");
            return myMsg;
        }
        myMsg.put("success", "1");
        return myMsg;
    }
}
