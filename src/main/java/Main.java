import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.util.Arrays;

public class Main {

    static final String REMOTE_SERVICE_URL = "https://api.nasa.gov/planetary/apod?api_key=rxvWjx7uegmFL5y96YnJbn1XZvNl9jNqjha7BqyK";
    public static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {

        httpClient();
    }

    static void httpClient() throws IOException {

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setUserAgent("My Test Service")
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build();

        HttpGet request = new HttpGet(REMOTE_SERVICE_URL);
        request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

        CloseableHttpResponse response = httpClient.execute(request);
        Arrays.stream(response.getAllHeaders()).forEach(System.out::println);

        NasaObject post = mapper.readValue(
                response.getEntity().getContent(),
                new TypeReference<>() {
                }
        );

        System.out.println(post);


        String[] fileName = post.getUrl().split("/");

        HttpGet request1 = new HttpGet(post.getUrl());
        request.setHeader(HttpHeaders.ACCEPT, ContentType.IMAGE_JPEG.getMimeType());

        CloseableHttpResponse response1 = httpClient.execute(request1);

        byte[] bytes = response1.getEntity().getContent().readAllBytes();

        try (FileOutputStream out = new FileOutputStream(fileName[6]);
             BufferedOutputStream bw = new BufferedOutputStream(out)) {
            bw.write(bytes, 0, bytes.length);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }


        response.close();
        httpClient.close();
    }
}
