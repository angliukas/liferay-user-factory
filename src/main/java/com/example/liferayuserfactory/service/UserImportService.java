package com.example.liferayuserfactory.service;

import com.example.liferayuserfactory.model.FailedRow;
import com.example.liferayuserfactory.model.ImportResult;
import com.example.liferayuserfactory.model.Organization;
import com.example.liferayuserfactory.model.UserRecord;
import com.example.liferayuserfactory.config.LiferayProperties;
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
    private final LiferayProperties properties;

    public UserImportService(ExcelUserParser parser, LiferayClient liferayClient, LiferayProperties properties) {
        this.parser = parser;
        this.liferayClient = liferayClient;
        this.properties = properties;
    }

    public ImportResult importUsers(MultipartFile file, Long organizationId) throws IOException {
        if (organizationId == null) {
            throw new IllegalArgumentException("Organization is required");
        }
        List<UserRecord> users = parser.parse(file.getInputStream());
        ImportResult result = new ImportResult();
        result.setTotalRows(users.size());

        List<FailedRow> failures = new ArrayList<>();
        int created = 0;
        List<Long> missingRoles;
        try {
            missingRoles = liferayClient.findMissingRoles(properties.getDefaultRoleIds());
        } catch (LiferayException e) {
            String reason = "Failed to validate roles: " + e.getMessage();
            LOGGER.error(reason);
            for (int i = 0; i < users.size(); i++) {
                failures.add(new FailedRow(i + 1, reason, users.get(i)));
            }
            result.setFailures(failures);
            return result;
        }
        for (int i = 0; i < users.size(); i++) {
            UserRecord record = users.get(i);
            if (record.getEmail() == null || record.getEmail().isBlank()) {
                failures.add(new FailedRow(i + 1, "Missing email", record));
                continue;
            }
            if (!missingRoles.isEmpty()) {
                failures.add(new FailedRow(i + 1, "Missing roles: " + missingRoles, record));
                continue;
            }
            try {
                if (liferayClient.userExists(record.getEmail())) {
                    failures.add(new FailedRow(i + 1, "User with this email already exists", record));
                    continue;
                }
            } catch (LiferayException e) {
                failures.add(new FailedRow(i + 1, "Failed to verify existing user: " + e.getMessage(), record));
                continue;
            }
            try {
                liferayClient.createUser(record, organizationId);
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
}
