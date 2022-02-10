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
@TableName("t_video")
public class Video implements Serializable {
    private String startTime;
    private Integer publisherId;
    @TableId(value = "videoId", type = IdType.AUTO)
    private Integer videoId;
    private String videoUrl;
    private String videoName;
    private String description;
    private Integer participantsNum;
}
