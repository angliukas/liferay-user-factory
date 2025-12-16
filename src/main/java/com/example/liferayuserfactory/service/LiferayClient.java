package com.example.liferayuserfactory.service;

import com.example.liferayuserfactory.model.UserRecord;
import com.example.liferayuserfactory.model.Organization;
import com.example.liferayuserfactory.model.Role;

import java.util.List;

public interface LiferayClient {
    void createUser(UserRecord record, Long organizationId, List<Long> roleIds) throws LiferayException;

    List<Organization> getOrganizations() throws LiferayException;

    List<Role> getRoles() throws LiferayException;
}
