package com.example.liferayuserfactory.service;

import com.example.liferayuserfactory.model.Organization;
import com.example.liferayuserfactory.model.UserRecord;

import java.util.List;

public interface LiferayClient {
    void createUser(UserRecord record, Long organizationId) throws LiferayException;

    List<Organization> getOrganizations() throws LiferayException;
}
