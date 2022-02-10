package com.jzh.lonershub.bean;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @version 1.0
 * @description
 * @Author Jiang Zhihang
 * @Date 2022/1/22 0:16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@TableName("t_global")
public class Global implements Serializable {
    private Long visitCount;
}
