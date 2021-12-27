package com.jzh.lonershub.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@TableName("t_message")
public class Message {
    @TableId(value = "messageId", type = IdType.AUTO)
    private Integer messageId;
    private Integer creatorId;
    private String content;
}
