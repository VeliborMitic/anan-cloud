package com.starlight.cdp.admin;

import de.codecentric.boot.admin.model.Application;
import de.codecentric.boot.admin.web.client.BasicAuthHttpHeaderProvider;
import de.codecentric.boot.admin.web.client.HttpHeadersProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoRestTemplateFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Description:
 *
 * @author fosin
 * @date 2018.8.12
 */
@Component
public class AdminHttpHeadersProvider implements HttpHeadersProvider {
    @Autowired
    private OAuth2RestTemplate oAuth2RestTemplate;

    @Override
    public HttpHeaders getHeaders(Application application) {
        String username = application.getMetadata().get("user.name");
        String password = application.getMetadata().get("user.password");

        HttpHeaders headers = new HttpHeaders();

        if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
            headers = new BasicAuthHttpHeaderProvider().getHeaders(application);
        } else {
            String authorization = null;
            HttpServletRequest httpServletRequest = getHttpServletRequest();
            if (httpServletRequest != null) {
                String accessToken = httpServletRequest.getParameter(OAuth2AccessToken.ACCESS_TOKEN);
                if (accessToken == null) {
                    authorization = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
                } else {
                    authorization = getAuthorization(OAuth2AccessToken.BEARER_TYPE, accessToken);
                }
            }
            if (authorization == null) {
                OAuth2AccessToken accessToken = oAuth2RestTemplate.getAccessToken();
                String accessTokenValue = accessToken.getValue();
                String tokenType = accessToken.getTokenType();
                if (StringUtils.hasText(accessTokenValue) && StringUtils.hasText(tokenType)) {
                    authorization = getAuthorization(tokenType, accessTokenValue);
                }
            }
            if (StringUtils.hasText(authorization)) {
                headers.set(HttpHeaders.AUTHORIZATION, authorization);
            }
        }

        return headers;
    }

    private String getAuthorization(String tokenType, String accessTokenValue) {
        return tokenType + " " + accessTokenValue;
    }

    private static HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        return null;
    }
}
