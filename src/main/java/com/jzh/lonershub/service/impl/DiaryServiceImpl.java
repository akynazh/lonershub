package com.jzh.lonershub.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzh.lonershub.bean.Diary;
import com.jzh.lonershub.mapper.DiaryMapper;
import com.jzh.lonershub.service.DiaryService;
import org.springframework.stereotype.Service;

@Service
public class DiaryServiceImpl extends ServiceImpl<DiaryMapper, Diary> implements DiaryService {
}
