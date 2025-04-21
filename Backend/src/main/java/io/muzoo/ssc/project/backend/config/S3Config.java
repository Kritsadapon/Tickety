package io.muzoo.ssc.project.backend.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.amazonaws.HttpMethod;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

@Configuration
public class S3Config {

    static Dotenv dotenv = Dotenv.load();
    static String accessKey =  dotenv.get("ACCESS_KEY");
    static String secretKey =  dotenv.get("SECRET_KEY");

    @Bean
    public S3Client s3Client() throws IOException {
//        String accessKey = new String(Files.readAllBytes(Paths.get("/Users/krits/Documents/GitHub/SSC Project/ssc-y24t2-backend-noidea/image-access-key"))).trim();
//        String secretKey = new String(Files.readAllBytes(Paths.get("/Users/krits/Documents/GitHub/SSC Project/ssc-y24t2-backend-noidea/image-secret-key"))).trim();

        return S3Client.builder()
                .endpointOverride(URI.create("https://user-profile-pictures.sgp1.digitaloceanspaces.com"))
                .region(Region.of("sgp1"))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
    }

    public static String generateImageUrl(String fileName) throws IOException {
        // Read the credentials from the files
//        String accessKey = new String(Files.readAllBytes(Paths.get("/Users/krits/Documents/GitHub/SSC Project/ssc-y24t2-backend-noidea/image-access-key"))).trim();
//        String secretKey = new String(Files.readAllBytes(Paths.get("/Users/krits/Documents/GitHub/SSC Project/ssc-y24t2-backend-noidea/image-secret-key"))).trim();

        // Initialize the S3 client
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(
                        new AmazonS3ClientBuilder.EndpointConfiguration("https://sgp1.digitaloceanspaces.com", "sgp1"))
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(accessKey, secretKey)))
                .build();

        // Generate a pre-signed URL with the correct path
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest("user-profile-pictures", "user-profile-pictures/profile-pictures/" + fileName)
                .withMethod(HttpMethod.GET);
                // .withExpiration(new java.util.Date(System.currentTimeMillis() + 3600 * 1000)); // 1-hour expiration

        URL preSignedUrl = s3Client.generatePresignedUrl(request);

        // Return the pre-signed URL as a string
        return preSignedUrl.toString();
    }
}