package com.example.liferayuserfactory.controller;

import com.example.liferayuserfactory.model.ImportResult;
import com.example.liferayuserfactory.model.Organization;
import com.example.liferayuserfactory.model.Role;
import com.example.liferayuserfactory.service.LiferayException;
import com.example.liferayuserfactory.service.UserImportService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/api/users")
public class UploadController {

    private final UserImportService importService;

    public UploadController(UserImportService importService) {
        this.importService = importService;
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<ImportResult> upload(@RequestParam("file") MultipartFile file,
                                               @RequestParam(value = "organizationId", required = false) Long organizationId,
                                               @RequestParam(value = "roleIds", required = false) List<Long> roleIds)
            throws IOException {
        ImportResult result = importService.importUsers(file, organizationId, roleIds);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/organizations")
    @ResponseBody
    public ResponseEntity<List<Organization>> organizations() throws LiferayException {
        return ResponseEntity.ok(importService.getOrganizations());
    }

    @GetMapping("/roles")
    @ResponseBody
    public ResponseEntity<List<Role>> roles() throws LiferayException {
        return ResponseEntity.ok(importService.getRoles());
    }
}
