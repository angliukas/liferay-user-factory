package com.example.liferayuserfactory.service;

import com.example.liferayuserfactory.config.LiferayProperties;
import com.example.liferayuserfactory.model.UserRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class JsonWsLiferayClient implements LiferayClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonWsLiferayClient.class);
    private final RestTemplate restTemplate;
    private final LiferayProperties properties;

    public JsonWsLiferayClient(RestTemplate restTemplate, LiferayProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @Override
    public void createUser(UserRecord record) throws LiferayException {
        if (properties.isDryRun()) {
            new LoggingLiferayClient().createUser(record);
            return;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("companyId", String.valueOf(properties.getCompanyId()));
        params.add("autoPassword", String.valueOf(properties.isAutoPassword()));
        params.add("password1", properties.getDefaultPassword());
        params.add("password2", properties.getDefaultPassword());
        params.add("autoScreenName", String.valueOf(properties.isAutoScreenName()));
        params.add("screenName", "");
        params.add("emailAddress", record.getEmail());
        params.add("facebookId", "0");
        params.add("openId", "");
        params.add("locale", properties.getLocale());
        params.add("firstName", record.getFirstName());
        params.add("middleName", "");
        params.add("lastName", record.getLastName());
        params.add("prefixId", "0");
        params.add("suffixId", "0");
        params.add("male", "true");
        params.add("birthdayMonth", String.valueOf(properties.getBirthdayMonth()));
        params.add("birthdayDay", String.valueOf(properties.getBirthdayDay()));
        params.add("birthdayYear", String.valueOf(properties.getBirthdayYear()));
        params.add("jobTitle", properties.getDefaultJobTitle());
        params.add("groupIds", "[]");
        params.add("organizationIds", "[]");
        params.add("roleIds", "[]");
        params.add("userGroupIds", "[]");
        params.add("sendEmail", "false");

        try {
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            String targetUrl = properties.getBaseUrl() + "/api/jsonws/user/add-user";
            ResponseEntity<String> response = restTemplate.postForEntity(targetUrl, request, String.class);
            LOGGER.info("Created user {} {} <{}> - response status {}", record.getFirstName(), record.getLastName(),
                    record.getEmail(), response.getStatusCode());
        } catch (RestClientException ex) {
            throw new LiferayException("Failed to create user via JSON WS", ex);
        }
    }
}
