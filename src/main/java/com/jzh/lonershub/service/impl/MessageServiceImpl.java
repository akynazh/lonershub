package com.jzh.lonershub.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzh.lonershub.bean.Message;
import com.jzh.lonershub.mapper.MessageMapper;
import com.jzh.lonershub.service.MessageService;
import org.springframework.stereotype.Service;

/**
 * @version 1.0
 * @description
 * @Author Jiang Zhihang
 * @Date 2021/12/31 22:40
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {
}
