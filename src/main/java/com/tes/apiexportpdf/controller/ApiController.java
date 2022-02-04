package com.tes.apiexportpdf.controller;

import com.tes.apiexportpdf.entity.GithubUsers;
import com.tes.apiexportpdf.entity.UserEndPoint;
import com.tes.apiexportpdf.service.ExportPdfService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ApiController {

    @Autowired
    private ExportPdfService exportPdfService;

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/getUser")
    ResponseEntity<UserEndPoint> getUser(){
        UserEndPoint userEndPoint = new UserEndPoint();
        userEndPoint.setLink("http://localhost:8080/downloadUserGithub");

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(userEndPoint);
    }

    @GetMapping("/downloadUserGithub")
    public void downloadUserGithub(HttpServletResponse response) throws IOException {
        Map<String, Object> data = getDataUserGithub();
        ByteArrayInputStream exportedData = exportPdfService.exportReceiptPdf("user_github", data);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=list-user.pdf");
        IOUtils.copy(exportedData, response.getOutputStream());
    }

    private Map<String, Object> getDataUserGithub() {
        Map<String, Object> data = new HashMap<>();

        String uri = "https://api.github.com/users?per_page=25";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization","token masukkan token github disini");
        HttpEntity<Void> requestEntity = new HttpEntity<>(httpHeaders);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<GithubUsers>> response = restTemplate.exchange(
                uri, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<GithubUsers>>(){});
        List<GithubUsers> githubUsers = response.getBody();

        data.put("githubUsers", githubUsers);
        return data;
    }

}
