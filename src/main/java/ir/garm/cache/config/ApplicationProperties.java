package ir.garm.cache.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final AuthInfo authInfo = new AuthInfo();
    private final UpstreamInfo upstreamInfo = new UpstreamInfo();

    @Data
    public static class AuthInfo {
        private String host;
        private String uri;
        private String clientId;
        private String clientSecret;
        private String grant_password;
        private String grant_refresh;
        private String username;
        private String password;
    }

    @Data
    public static class UpstreamInfo {
        private String host;
        private String uri;
        private String encoding;
    }

}
