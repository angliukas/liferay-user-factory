package com.example.liferayuserfactory.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "liferay")
public class LiferayProperties {

    private String baseUrl = "http://localhost:8080";
    private String adminUsername = "test@liferay.com";
    private String adminPassword = "test";
    private long companyId = 0L;
    private boolean dryRun = true;
    private boolean autoPassword = true;
    private String defaultPassword = "liferay";
    private boolean autoScreenName = true;
    private String defaultJobTitle = "Imported";
    private int birthdayMonth = 0;
    private int birthdayDay = 1;
    private int birthdayYear = 1970;
    private String locale = "en_US";

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public boolean isDryRun() {
        return dryRun;
    }

    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

    public boolean isAutoPassword() {
        return autoPassword;
    }

    public void setAutoPassword(boolean autoPassword) {
        this.autoPassword = autoPassword;
    }

    public String getDefaultPassword() {
        return defaultPassword;
    }

    public void setDefaultPassword(String defaultPassword) {
        this.defaultPassword = defaultPassword;
    }

    public boolean isAutoScreenName() {
        return autoScreenName;
    }

    public void setAutoScreenName(boolean autoScreenName) {
        this.autoScreenName = autoScreenName;
    }

    public String getDefaultJobTitle() {
        return defaultJobTitle;
    }

    public void setDefaultJobTitle(String defaultJobTitle) {
        this.defaultJobTitle = defaultJobTitle;
    }

    public int getBirthdayMonth() {
        return birthdayMonth;
    }

    public void setBirthdayMonth(int birthdayMonth) {
        this.birthdayMonth = birthdayMonth;
    }

    public int getBirthdayDay() {
        return birthdayDay;
    }

    public void setBirthdayDay(int birthdayDay) {
        this.birthdayDay = birthdayDay;
    }

    public int getBirthdayYear() {
        return birthdayYear;
    }

    public void setBirthdayYear(int birthdayYear) {
        this.birthdayYear = birthdayYear;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
