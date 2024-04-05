package org.example.resttemplatewebclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Spring RestTemplate 로깅 인터셉터
 *
 * @author Hoonmaro
 */
@Slf4j
@Configuration
public class RestTemplateLoggingRequestInterceptor implements ClientHttpRequestInterceptor {

    /**
     * <pre>
     * RestTemplate 로깅 Interceptor
     *
     * <pre>
     *
     * @param request HttpRequest
     * @param body Request Body
     * @param execution ClientHttpRequestExecution
     * @throws IOException
     */
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        // request log
        URI uri = request.getURI();
        traceRequest(request, body);

        // execute
        ClientHttpResponse response = execution.execute(request, body);

        // response log
        traceResponse(response, uri);
        return response;
    }

    /**
     * <pre>
     * RestTemplate Request 로깅
     *
     * <pre>
     * @param request HttpRequest
     * @param body Request Body
     */
    private void traceRequest(HttpRequest request, byte[] body) {
        StringBuilder reqLog = new StringBuilder();
        reqLog.append("[REQUEST] ")
        .append("Uri : ").append(request.getURI())
        .append(", Method : ").append(request.getMethod())
        .append(", Request Body : ").append(new String(body, StandardCharsets.UTF_8));
        log.info(reqLog.toString());
    }

    /**
     * <pre>
     * RestTemplate Response 로깅
     *
     * <pre>
     * @param response ClientHttpResponse
     * @throws IOException
     */
    private void traceResponse(ClientHttpResponse response, URI uri) throws IOException {
        StringBuilder resLog = new StringBuilder();
        resLog.append("[RESPONSE] ")
        .append("Uri : ").append(uri)
        .append(", Status code : ").append(response.getStatusCode())
        .append(", Response Body : ").append(StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8));
        log.info(resLog.toString());
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        if(log.isDebugEnabled()){
            return restTemplateBuilder
                    // 로깅 인터셉터에서 Stream을 소비하므로 BufferingClientHttpRequestFactory 을 꼭 써야한다.
                    .requestFactory(() -> new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()))
                    .setConnectTimeout(Duration.ofMillis(3000))
                    .setReadTimeout(Duration.ofMillis(3000))
                    .additionalMessageConverters(new StringHttpMessageConverter(Charset.forName("UTF-8")))
                    // 로깅 인터셉터 설정
                    .additionalInterceptors(new RestTemplateLoggingRequestInterceptor())
                    .build();
        }
        //디버그상황이 아니면 버퍼는 성능이슈가 생긴다.
        return restTemplateBuilder.requestFactory(SimpleClientHttpRequestFactory::new).build();
    }
}