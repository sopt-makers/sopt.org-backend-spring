package sopt.org.homepage.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {

    @Value("${aws.region}")
    private String region;

    @Value("${aws.credentials.access-key:}")
    private String accessKey;

    @Value("${aws.credentials.secret-key:}")
    private String secretKey;

    /**
     * S3Client Bean 파일 업로드, 삭제 등 S3 작업에 사용
     */
    @Bean
    public S3Client s3Client() {
        String resolvedAccessKey = resolveAccessKey();
        String resolvedSecretKey = resolveSecretKey();

        if (hasCredentials(resolvedAccessKey, resolvedSecretKey)) {
            // 명시적 자격 증명 사용 (EC2, 로컬)
            return S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(
                            StaticCredentialsProvider.create(
                                    AwsBasicCredentials.create(resolvedAccessKey, resolvedSecretKey)
                            )
                    )
                    .build();
        }

        // IAM Role 사용 (Lambda 기본 역할)
        return S3Client.builder()
                .region(Region.of(region))
                .build();
    }

    /**
     * S3Presigner Bean Presigned URL 생성에 사용
     */
    @Bean
    public S3Presigner s3Presigner() {
        String resolvedAccessKey = resolveAccessKey();
        String resolvedSecretKey = resolveSecretKey();

        S3Presigner.Builder builder = S3Presigner.builder()
                .region(Region.of(region));

        if (hasCredentials(resolvedAccessKey, resolvedSecretKey)) {
            // 명시적 자격 증명 사용 (EC2, 로컬)
            builder.credentialsProvider(
                    StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(resolvedAccessKey, resolvedSecretKey)
                    )
            );
        }
        // else: IAM Role 사용 (Lambda 기본 역할)

        return builder.build();
    }

    /**
     * AWS Access Key 해석 Lambda 환경에서는 APP_AWS_ACCESS_KEY_ID 환경변수 사용 (예약어 회피)
     */
    private String resolveAccessKey() {
        // Lambda 환경변수 우선 확인
        String lambdaAccessKey = System.getenv("APP_AWS_ACCESS_KEY_ID");
        if (lambdaAccessKey != null && !lambdaAccessKey.isEmpty()) {
            return lambdaAccessKey;
        }
        // application.yml 설정값 사용
        return accessKey;
    }

    /**
     * AWS Secret Key 해석 Lambda 환경에서는 APP_AWS_SECRET_ACCESS_KEY 환경변수 사용 (예약어 회피)
     */
    private String resolveSecretKey() {
        // Lambda 환경변수 우선 확인
        String lambdaSecretKey = System.getenv("APP_AWS_SECRET_ACCESS_KEY");
        if (lambdaSecretKey != null && !lambdaSecretKey.isEmpty()) {
            return lambdaSecretKey;
        }
        // application.yml 설정값 사용
        return secretKey;
    }

    /**
     * 자격 증명 존재 여부 확인
     */
    private boolean hasCredentials(String accessKey, String secretKey) {
        return accessKey != null && !accessKey.isEmpty()
                && secretKey != null && !secretKey.isEmpty();
    }
}
