package com.example.liferayuserfactory.service;

import com.example.liferayuserfactory.model.FailedRow;
import com.example.liferayuserfactory.model.ImportResult;
import com.example.liferayuserfactory.model.Organization;
import com.example.liferayuserfactory.model.Role;
import com.example.liferayuserfactory.model.UserRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserImportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserImportService.class);
    private final ExcelUserParser parser;
    private final LiferayClient liferayClient;

    public UserImportService(ExcelUserParser parser, JsonWsLiferayClient liferayClient) {
        this.parser = parser;
        this.liferayClient = liferayClient;
    }

    public ImportResult importUsers(MultipartFile file, Long organizationId, List<Long> roleIds) throws IOException {
        List<UserRecord> users = parser.parse(file.getInputStream());
        ImportResult result = new ImportResult();
        result.setTotalRows(users.size());

        List<FailedRow> failures = new ArrayList<>();
        int created = 0;
        for (int i = 0; i < users.size(); i++) {
            UserRecord record = users.get(i);
            if (record.getEmail() == null || record.getEmail().isBlank()) {
                failures.add(new FailedRow(i + 1, "Missing email", record));
                continue;
            }
            try {
                liferayClient.createUser(record, organizationId, roleIds);
                created++;
            } catch (LiferayException e) {
                LOGGER.error("Unable to create user {}: {}", record.getEmail(), e.getMessage());
                failures.add(new FailedRow(i + 1, e.getMessage(), record));
            }
        }
        result.setCreated(created);
        result.setFailures(failures);
        return result;
    }

    public List<Organization> getOrganizations() throws LiferayException {
        return liferayClient.getOrganizations();
    }

    public List<Role> getRoles() throws LiferayException {
        return liferayClient.getRoles();
    }
}
