package com.lx.leyou.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


@Service
public class UploadService {

    private final List<String> CONTENT_TYPE = Arrays.asList("application/x-png", "application/x-jpg");
    private final Logger LOGGER = LoggerFactory.getLogger(UploadService.class);

    @Autowired
    private FastFileStorageClient storageClient;

    public String queryImage(MultipartFile file) {

        //验证文件类型
        String originalFilename = file.getOriginalFilename();
        if (CONTENT_TYPE.contains(originalFilename)) {
            LOGGER.info("文件类型不合法：{}", originalFilename);
            return null;
        }

        try {
            //验证文件内容
            BufferedImage read = ImageIO.read(file.getInputStream());
            if (read == null) {
                LOGGER.info("文件内容不合法：{}", originalFilename);
                return null;
            }

            //保存到服务器
            /*file.transferTo(new File("D:\\WorkResource\\IdeaProjects\\images\\" + originalFilename));
            System.out.println("执行成功");*/
            String ext = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
            StorePath storePath = this.storageClient.uploadFile(file.getInputStream(), file.getSize(), ext, null);

            //返回url
            return "http://image.leyou.com/" + storePath.getFullPath();
        } catch (IOException e) {
            LOGGER.info("服务器内部错误：{}", originalFilename);
            e.printStackTrace();
        }
        return null;
    }
}
