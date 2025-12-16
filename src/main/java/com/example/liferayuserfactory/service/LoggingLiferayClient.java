package com.example.liferayuserfactory.service;

import com.example.liferayuserfactory.model.UserRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingLiferayClient implements LiferayClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingLiferayClient.class);

    @Override
    public void createUser(UserRecord record) {
        LOGGER.info("[DRY RUN] Would create user: {} {} <{}>", record.getFirstName(), record.getLastName(), record.getEmail());
    }
}
