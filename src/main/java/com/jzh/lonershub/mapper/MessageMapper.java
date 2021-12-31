package com.jzh.lonershub.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jzh.lonershub.bean.Message;
import org.apache.ibatis.annotations.Mapper;

/**
 * @version 1.0
 * @description
 * @Author Jiang Zhihang
 * @Date 2021/12/31 22:41
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}
