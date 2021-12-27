package com.jzh.lonershub.bean;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@TableName("t_loner")
public class Loner {
    private Integer lonerId;
    private String lonerName;
    private String lonerPassword;
    private String lonerEmail;
    private String lonerAvatarUrl;
    private String lonerSignature;
}