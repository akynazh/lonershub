package com.jzh.lonershub.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Message {
    private Integer messageId;
    private Integer creatorId;
    private String content;
}
