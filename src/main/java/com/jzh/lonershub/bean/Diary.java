package com.jzh.lonershub.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Diary {
    private Integer creatorId;
    private Integer diaryId;
    private String createTime;
    private String content;
}
