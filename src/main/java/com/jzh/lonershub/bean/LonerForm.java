package com.jzh.lonershub.bean;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@TableName("t_loner")
public class LonerForm {
    private Integer lonerId;

    @NotBlank(message = "用户名不能为空")
    @Length(max = 32, message = "用户名不能大于32位")
    private String lonerName;

    @NotBlank(message = "密码不能为空")
    @Length(max = 32, message = "密码不能大于32位")
    private String lonerPassword;

    @NotBlank(message = "邮箱不能为空")
    private String lonerEmail;

    private MultipartFile lonerAvatar;

    @Length(max = 30, message = "个性签名不能大于30个字段")
    private String lonerSignature;
}