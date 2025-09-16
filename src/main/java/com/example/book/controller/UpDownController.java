package com.example.book.controller;

import com.example.book.dto.upload.UploadFileDTO;
import com.example.book.dto.upload.UploadResultDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@Log4j2
public class UpDownController {
    @Value("${org.zerock.upload.path}") //파일을 업로드하는 경로
    private String uploadPath;

    @Operation(description = "POST 방식으로 파일 업로드")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<UploadResultDTO> upload(UploadFileDTO uploadFileDTO){

        log.info(uploadFileDTO);
        if(uploadFileDTO.getFiles()!=null){
            final List<UploadResultDTO> list = new ArrayList<>();

            uploadFileDTO.getFiles().forEach(multipartFile -> {
                String originalName = multipartFile.getOriginalFilename();
                log.info(originalName);
                String uuid = UUID.randomUUID().toString(); //랜덤 uuid 생성
                Path savePath = Paths.get(uploadPath, uuid+"_"+originalName); //uuid 붙인 파일명으로 바꿈
                boolean image = false;
                try{
                    multipartFile.transferTo(savePath); //실제 파일 저장
                    if(Files.probeContentType(savePath).startsWith("image")){
                        image = true;
                        File thumbnailFile = new File(uploadPath,"s_"+uuid+"_"+originalName);
                        Thumbnailator.createThumbnail(savePath.toFile(),thumbnailFile,200,200);
                    }
                    list.add(UploadResultDTO.builder()
                            .uuid(uuid)
                            .fileName(originalName)
                            .img(image).build());
                }catch (IOException e){
                    e.printStackTrace();
                }

            });
            return list;
        }
        return null;
    }
    @Operation(description = "GET방식으로 첨부파일 조회")
    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFileGET(@PathVariable String fileName){
        Resource resource = new FileSystemResource(uploadPath+File.separator+fileName);
        String resourceName = resource.getFilename();
        HttpHeaders headers = new HttpHeaders();
        try{
            headers.add("Content-Type",Files.probeContentType(resource.getFile().toPath()));
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().headers(headers).body(resource);
    }
    @Operation(description = "DELETE방식으로 파일 삭제")
    @DeleteMapping("/remove/{fileName}")
    public Map<String,Boolean> removeFile(@PathVariable String fileName){
        Resource resource = new FileSystemResource(uploadPath+File.separator+fileName);
        log.info(resource);
        String resourceName = resource.getFilename();
        Map<String, Boolean> resultMap = new HashMap<>();
        boolean removed = false;
        try{
            String contentType = Files.probeContentType(resource.getFile().toPath());
            log.info(resource.getFile().toPath());
            removed = resource.getFile().delete();
            if(contentType.startsWith("image")){
                File thumbnailFile = new File(uploadPath+File.separator+"s_"+fileName);
                thumbnailFile.delete();
            }
        }catch (Exception e){
            log.error(e);
        }
        resultMap.put("result",removed);
        return resultMap;
    }
}
