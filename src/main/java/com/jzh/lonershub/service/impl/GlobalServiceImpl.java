package com.jzh.lonershub.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzh.lonershub.bean.Global;
import com.jzh.lonershub.mapper.GlobalMapper;
import com.jzh.lonershub.service.GlobalService;
import org.springframework.stereotype.Service;

/**
 * @version 1.0
 * @description
 * @Author Jiang Zhihang
 * @Date 2022/1/22 0:25
 */
@Service
public class GlobalServiceImpl extends ServiceImpl<GlobalMapper, Global> implements GlobalService {
}
