package sopt.org.homepage;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * AWS Lambda 진입점 (Entry Point) API Gateway로부터 받은 요청을 Spring Boot 애플리케이션으로 라우팅합니다. SpringBootLambdaContainerHandler가
 * Spring 컨텍스트를 초기화하고 요청을 적절한 Controller로 전달합니다. SAM Template에서 Handler로 지정: sopt.org.homepage.LambdaHandler
 */
public class LambdaHandler implements RequestStreamHandler {

    private static final SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;

    static {
        try {
            handler = SpringBootLambdaContainerHandler
                    .getAwsProxyHandler(HomepageApplication.class);
        } catch (ContainerInitializationException e) {
            throw new RuntimeException("Spring Boot 애플리케이션 초기화 실패", e);
        }
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context)
            throws IOException {
        handler.proxyStream(inputStream, outputStream, context);
    }
}
