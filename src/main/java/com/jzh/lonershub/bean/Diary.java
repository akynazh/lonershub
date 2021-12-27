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
@TableName("t_diary")
public class Diary {
    private Integer creatorId;
    private Integer diaryId;
    private String createTime;
    private String content;
}
