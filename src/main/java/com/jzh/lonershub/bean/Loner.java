package com.jzh.lonershub.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Loner {
    private Integer lonerId;
    private String lonerName;
    private String lonerPassword;
    private String lonerEmail;
    private String lonerResidence;
    private String lonerAvatarUrl;
}