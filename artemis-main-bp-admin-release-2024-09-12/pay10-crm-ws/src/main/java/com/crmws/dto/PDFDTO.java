package com.crmws.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PDFDTO {
    private MultipartFile pdf1;
    private MultipartFile pdf2;

    private String user;


}
