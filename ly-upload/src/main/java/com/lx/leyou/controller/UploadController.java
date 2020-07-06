package com.lx.leyou.controller;

import com.lx.leyou.service.UploadService;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("upload")
public class UploadController {

    @Autowired
    public UploadService uploadService;

    @PostMapping("image")
    public ResponseEntity<String> queryImage(@RequestParam("file") MultipartFile file) {
        System.out.println("进入图片");
        String url = this.uploadService.queryImage(file);
        if (StringUtils.isBlank(url)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(url);
    }
}
