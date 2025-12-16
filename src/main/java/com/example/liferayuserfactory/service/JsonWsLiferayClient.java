package com.example.liferayuserfactory.service;

import com.example.liferayuserfactory.config.LiferayProperties;
import com.example.liferayuserfactory.model.Organization;
import com.example.liferayuserfactory.model.UserRecord;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.text.MessageFormat;

@Component
public class JsonWsLiferayClient implements LiferayClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonWsLiferayClient.class);
    private final RestTemplate restTemplate;
    private final LiferayProperties properties;
    private final ObjectMapper objectMapper;
    private String accountCreatedTemplate;

    public JsonWsLiferayClient(RestTemplate restTemplate, LiferayProperties properties, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    public void createUser(UserRecord record, Long organizationId) throws LiferayException {
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
        params.add("organizationIds", toJsonArray(organizationId));
        params.add("roleIds", toJsonArray(properties.getDefaultRoleIds()));
        params.add("userGroupIds", "[]");
        params.add("sendEmail", "false");

        try {
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            String targetUrl = properties.getBaseUrl() + "/api/jsonws/user/add-user";
            ResponseEntity<String> response = restTemplate.postForEntity(targetUrl, request, String.class);
            LOGGER.info("Created user {} {} <{}> - response status {}", record.getFirstName(), record.getLastName(),
                    record.getEmail(), response.getStatusCode());
            logAccountCreatedNotification(record);
        } catch (RestClientException ex) {
            throw new LiferayException("Failed to create user via JSON WS", ex);
        }
    }

    @Override
    public List<Organization> getOrganizations() throws LiferayException {
        String targetUrl = properties.getBaseUrl()
                + "/api/jsonws/organization/get-organizations?companyId="
                + properties.getCompanyId()
                + "&parentOrganizationId=0&start=-1&end=-1";
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(targetUrl, String.class);
            return mapArray(response.getBody(), "organizationId", "name", Organization::new);
        } catch (Exception ex) {
            throw new LiferayException("Failed to fetch organizations from Liferay", ex);
        }
    }

    @Override
    public boolean userExists(String email) throws LiferayException {
        String targetUrl = UriComponentsBuilder.fromHttpUrl(properties.getBaseUrl())
                .path("/api/jsonws/user/get-user-by-email-address")
                .queryParam("companyId", properties.getCompanyId())
                .queryParam("emailAddress", email)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString();
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(targetUrl, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                return false;
            }
            String body = response.getBody();
            if (body == null || body.isBlank()) {
                return false;
            }
            try {
                JsonNode root = objectMapper.readTree(body);
                if (root.has("exception")) {
                    LOGGER.info("User not found: {} ({}), treating as non-existent", email, root.path("exception").asText());
                    return false;
                }
                return true;
            } catch (java.io.IOException e) {
                throw new LiferayException("Failed to parse user existence response", e);
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return false;
            }
            throw new LiferayException("Failed to check if user exists", e);
        } catch (RestClientException ex) {
            throw new LiferayException("Failed to check if user exists", ex);
        }
    }

    @Override
    public List<Long> findMissingRoles(List<Long> roleIds) throws LiferayException {
        List<Long> missing = new ArrayList<>();
        if (roleIds == null) {
            return missing;
        }
        for (Long roleId : roleIds) {
            if (roleId == null) {
                continue;
            }
            if (!roleExists(roleId)) {
                missing.add(roleId);
            }
        }
        return missing;
    }

    private String toJsonArray(Long value) {
        if (value == null) {
            return "[]";
        }
        return '[' + String.valueOf(value) + ']';
    }

    private String toJsonArray(List<Long> values) {
        if (values == null || values.isEmpty()) {
            return "[]";
        }
        return values.toString();
    }

    private <T> List<T> mapArray(String body, String idField, String nameField, EntityMapper<T> mapper)
            throws java.io.IOException {
        if (body == null || body.isBlank()) {
            return Collections.emptyList();
        }
        JsonNode root = objectMapper.readTree(body);
        if (!(root instanceof ArrayNode arrayNode)) {
            return Collections.emptyList();
        }
        List<T> mapped = new ArrayList<>();
        for (JsonNode node : arrayNode) {
            long id = node.path(idField).asLong();
            String name = node.path(nameField).asText();
            mapped.add(mapper.map(id, name));
        }
        return mapped;
    }

    @FunctionalInterface
    private interface EntityMapper<T> {
        T map(long id, String name);
    }

    private boolean roleExists(long roleId) throws LiferayException {
        String targetUrl = UriComponentsBuilder.fromHttpUrl(properties.getBaseUrl())
                .path("/api/jsonws/role/get-role")
                .queryParam("roleId", roleId)
                .toUriString();
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(targetUrl, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return false;
            }
            throw new LiferayException("Failed to verify role existence", e);
        } catch (RestClientException ex) {
            throw new LiferayException("Failed to verify role existence", ex);
        }
    }

    private void logAccountCreatedNotification(UserRecord record) {
        try {
            String template = loadAccountCreatedTemplate();
            String formattedTemplate = MessageFormat.format(template,
                    record.getFirstName(),
                    record.getLastName(),
                    record.getEmail(),
                    properties.getBaseUrl());
            LOGGER.info("Account Created Notification template:\n{}", formattedTemplate);
        } catch (IOException e) {
            LOGGER.warn("Unable to read Account Created Notification template", e);
        }
    }

    private String loadAccountCreatedTemplate() throws IOException {
        if (accountCreatedTemplate == null) {
            try (var inputStream = getClass().getClassLoader()
                    .getResourceAsStream("templates/account-created-notification.txt")) {
                if (inputStream == null) {
                    throw new IOException("Account Created Notification template not found on classpath");
                }
                accountCreatedTemplate = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            }
        }
        return accountCreatedTemplate;
    }
}
