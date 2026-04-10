package com.ucacue.UcaApp.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {

    private String contextPath = "/ucacue";
    private boolean cookieSecure = false;
    private String refreshTokenCookie = "refreshToken";
    private Duration accessTokenDuration = Duration.ofMinutes(5);
    private Duration refreshTokenDuration = Duration.ofDays(7);
    private String refreshTokenSameSite = "Lax";
    private String csrfCookie = "XSRF-TOKEN";
    private String csrfHeader = "X-XSRF-TOKEN";
    private String privateKey = "";
    private String userGenerator = "";
    private int maxSessions = 1;

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getUserGenerator() {
        return userGenerator;
    }

    public void setUserGenerator(String userGenerator) {
        this.userGenerator = userGenerator;
    }

    public int getMaxSessions() {
        return maxSessions;
    }

    public void setMaxSessions(int maxSessions) {
        this.maxSessions = maxSessions;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public boolean isCookieSecure() {
        return cookieSecure;
    }

    public void setCookieSecure(boolean cookieSecure) {
        this.cookieSecure = cookieSecure;
    }

    public String getRefreshTokenCookie() {
        return refreshTokenCookie;
    }

    public void setRefreshTokenCookie(String refreshTokenCookie) {
        this.refreshTokenCookie = refreshTokenCookie;
    }

    public Duration getAccessTokenDuration() {
        return accessTokenDuration;
    }

    public void setAccessTokenDuration(Duration accessTokenDuration) {
        this.accessTokenDuration = accessTokenDuration;
    }

    public Duration getRefreshTokenDuration() {
        return refreshTokenDuration;
    }

    public void setRefreshTokenDuration(Duration refreshTokenDuration) {
        this.refreshTokenDuration = refreshTokenDuration;
    }

    public String getRefreshTokenSameSite() {
        return refreshTokenSameSite;
    }

    public void setRefreshTokenSameSite(String refreshTokenSameSite) {
        this.refreshTokenSameSite = refreshTokenSameSite;
    }

    public String getCsrfCookie() {
        return csrfCookie;
    }

    public void setCsrfCookie(String csrfCookie) {
        this.csrfCookie = csrfCookie;
    }

    public String getCsrfHeader() {
        return csrfHeader;
    }

    public void setCsrfHeader(String csrfHeader) {
        this.csrfHeader = csrfHeader;
    }

    // public String getRefreshTokenPath() {
    //     return "/back-end-auth";
    // }

    // public String getCsrfCookiePath() {
    //     return "/";
    // }

    public String getRefreshTokenPath() {
        return contextPath + "/auth";
    }

    public String getCsrfCookiePath() {
        return contextPath + "/auth";
    }
}