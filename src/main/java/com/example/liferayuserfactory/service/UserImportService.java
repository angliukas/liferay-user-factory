package com.example.liferayuserfactory.service;

import com.example.liferayuserfactory.model.FailedRow;
import com.example.liferayuserfactory.model.ImportResult;
import com.example.liferayuserfactory.model.LiferayUser;
import com.example.liferayuserfactory.model.Organization;
import com.example.liferayuserfactory.model.UserRecord;
import com.example.liferayuserfactory.config.LiferayProperties;
import com.example.liferayuserfactory.repository.LiferayUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class UserImportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserImportService.class);
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);
    private final ExcelUserParser parser;
    private final LiferayClient liferayClient;
    private final LiferayProperties properties;
    private final LiferayUserRepository userRepository;

    public UserImportService(ExcelUserParser parser, LiferayClient liferayClient, LiferayProperties properties,
                             LiferayUserRepository userRepository) {
        this.parser = parser;
        this.liferayClient = liferayClient;
        this.properties = properties;
        this.userRepository = userRepository;
    }

    public ImportResult importUsers(MultipartFile file, Long organizationId) throws IOException {
        if (organizationId == null) {
            throw new IllegalArgumentException("Organization is required");
        }
        List<UserRecord> users = parser.parse(file.getInputStream());
        ImportResult result = new ImportResult();
        result.setTotalRows(users.size());

        List<FailedRow> failures = new ArrayList<>();
        List<FailedRow> validationErrors = new ArrayList<>();
        int created = 0;
        List<String> createdEmails = new ArrayList<>();
        List<String> existingEmails = new ArrayList<>();
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
                validationErrors.add(new FailedRow(i + 1, "Missing email", record));
                continue;
            }
            if (!isValidEmail(record.getEmail())) {
                validationErrors.add(new FailedRow(i + 1, "Invalid email format", record));
                continue;
            }
            if (!missingRoles.isEmpty()) {
                failures.add(new FailedRow(i + 1, "Missing roles: " + missingRoles, record));
                continue;
            }
            try {
                if (userRepository.existsByEmailAddressIgnoreCase(record.getEmail())
                        || liferayClient.userExists(record.getEmail())) {
                    existingEmails.add(record.getEmail());
                    continue;
                }
            } catch (LiferayException e) {
                String reason = "Unable to verify user existence: " + e.getMessage();
                LOGGER.error("{}", reason);
                failures.add(new FailedRow(i + 1, reason, record));
                continue;
            }
            try {
                liferayClient.createUser(record, organizationId);
                created++;
                createdEmails.add(record.getEmail());
            } catch (LiferayException e) {
                LOGGER.error("Unable to create user {}: {}", record.getEmail(), e.getMessage());
                failures.add(new FailedRow(i + 1, e.getMessage(), record));
            }
        }
        result.setCreated(created);
        result.setFailures(failures);
        result.setValidationErrors(validationErrors);
        result.setImportedUsers(readImportedUsers(createdEmails));
        result.setExistingUsers(readImportedUsers(existingEmails));
        return result;
    }

    private List<LiferayUser> readImportedUsers(List<String> createdEmails) {
        List<LiferayUser> importedUsers = new ArrayList<>();
        if (createdEmails.isEmpty()) {
            return importedUsers;
        }
        userRepository.findByEmailAddressIgnoreCaseIn(createdEmails)
                .forEach(importedUsers::add);
        return importedUsers;
    }

    public List<Organization> getOrganizations() throws LiferayException {
        return liferayClient.getOrganizations();
    }

    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
}
