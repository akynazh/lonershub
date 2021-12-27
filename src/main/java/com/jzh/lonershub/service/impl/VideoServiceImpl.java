package com.jzh.lonershub.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzh.lonershub.bean.Video;
import com.jzh.lonershub.mapper.VideoMapper;
import com.jzh.lonershub.service.VideoService;
import org.springframework.stereotype.Service;

@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService{
}
