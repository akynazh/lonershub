package com.jzh.lonershub.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @version 1.0
 * @description video参与者
 * @Author Jiang Zhihang
 * @Date 2021/12/30 21:53
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@TableName("t_participant")
public class Participant implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Integer videoId;
    private Integer participantId;
}
