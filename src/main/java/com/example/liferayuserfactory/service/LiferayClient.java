package com.example.liferayuserfactory.service;

import com.example.liferayuserfactory.model.UserRecord;

public interface LiferayClient {
    void createUser(UserRecord record) throws LiferayException;
}
