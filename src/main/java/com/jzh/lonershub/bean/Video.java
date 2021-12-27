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
@TableName("t_video")
public class Video {
    private String startTime;
    private Integer publisherId;
    private Integer videoId;
    private String videoUrl;
    private String description;
}
