package com.jzh.lonershub.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@TableName("t_message")
public class Message implements Serializable {
    @TableId(value = "messageId", type = IdType.AUTO)
    private Integer messageId;
    private Integer creatorId;
    private String creatorName;
    private String createTime;
    private Integer videoId;
    private String content;
}
