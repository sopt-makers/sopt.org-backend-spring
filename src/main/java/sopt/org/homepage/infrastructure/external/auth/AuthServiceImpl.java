package sopt.org.homepage.infrastructure.external.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sopt.org.homepage.global.config.AuthConfig;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthClient authClient;
    private final AuthConfig authConfig;

    @Override
    public int getUserCountByGeneration(Integer generation) {
        try {
            var response = authClient.getUserCount(
                    generation,
                    authConfig.getAuthApiKey(),
                    authConfig.getAuthServiceName()
            );

            if (response.success() && response.data() != null) {
                return response.data().numberOfMembersAtGeneration();
            }

            log.warn("Failed to get user count for generation {}: {}", generation, response.message());
            return 0;

        } catch (Exception e) {
            log.error("Failed to get user count for generation {}", generation, e);
            return 0;
        }
    }
}
