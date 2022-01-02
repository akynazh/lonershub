package com.jzh.lonershub.util;

import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @version 1.0
 * @description
 * @Author Jiang Zhihang
 * @Date 2022/1/2 22:47
 */
public class FileUtils {

    /**
     * @description: 获取文件后缀名
     * @author Jiang Zhihang
     * @date 2022/1/2 23:37
     */
    public static String getFileType(MultipartFile file) {
        String type = file.getContentType();
        String fileType;
        if (type == null) return null;
        else {
            // type -> img/jpg
            fileType = type.substring(type.indexOf('/') + 1);
            return fileType;
        }
    }

    /**
     * @description: 获取上传路径
     * @author Jiang Zhihang
     * @date 2022/1/2 23:32
     * @param type 上传类型，如avatar，video
     * @param fileName 上传文件的名称
     */
    public static String getUploadPath(String type, String fileName) throws FileNotFoundException, UnsupportedEncodingException {
        String osName = System.getProperty("os.name");

        if (osName.equals("Linux")) { // 部署于Linux主机上时获取的上传路径
            // 返回/app/static/${uploadType}/${fileName}
            // e.g. /app/static/avatar/test.jpg
            return File.separator + "app/static/" + type + "/" + fileName;
        } else {
            // 获取上传地址
            File relativePathFile = new File(URLDecoder.decode(ResourceUtils.getURL("classpath:").getPath(), "utf-8"));
            String absolutePath = relativePathFile.getAbsoluteFile().getPath();
            // 获取得路径为：
            // ${project}/target/classes
            // 或
            // /root/file:/root/${jar包名}!/BOOT-INF/classes!  [centos7.9下]

            // 返回 ${project}/target/classes/static/${uploadType}/${fileName}
            // e.g. ${project}/target/classes/static/avatar/test.jpg
            return absolutePath + "/static/" + type + "/" + fileName;
        }
    }
}
