package com.example.liferayuserfactory.service;

import com.example.liferayuserfactory.model.Organization;
import com.example.liferayuserfactory.model.UserRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingLiferayClient implements LiferayClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingLiferayClient.class);

    @Override
    public void createUser(UserRecord record, Long organizationId) {
        LOGGER.info("Would create user: {} {} <{}> in organization {}", record.getFirstName(),
                record.getLastName(), record.getEmail(), organizationId);
    }

    @Override
    public java.util.List<Organization> getOrganizations() {
        return java.util.Collections.emptyList();
    }

    @Override
    public boolean userExists(String email) {
        LOGGER.info("Would check if user exists: {}", email);
        return false;
    }

    @Override
    public java.util.List<Long> findMissingRoles(java.util.List<Long> roleIds) {
        LOGGER.info("Would verify roles exist: {}", roleIds);
        return java.util.Collections.emptyList();
    }
}
