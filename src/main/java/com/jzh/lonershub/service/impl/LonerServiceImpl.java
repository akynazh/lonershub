package com.jzh.lonershub.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzh.lonershub.bean.Loner;
import com.jzh.lonershub.mapper.LonerMapper;
import com.jzh.lonershub.service.LonerService;
import org.springframework.stereotype.Service;

@Service
public class LonerServiceImpl extends ServiceImpl<LonerMapper, Loner> implements LonerService {
}
