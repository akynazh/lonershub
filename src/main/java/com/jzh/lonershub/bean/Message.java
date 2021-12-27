package com.jzh.lonershub.bean;

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
    private Integer messageId;
    private Integer creatorId;
    private String content;
}
