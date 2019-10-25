package com.example.minio;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.MinioClient;
import io.minio.errors.MinioException;

@Configuration
public class MinioConfig 
{    
    private MinioClient client;

    @Bean
    public MinioClient minioClient() 
    {
        try
        {
            client = new MinioClient("http://127.0.0.1:9000", "admin","Password123");
        }
         catch (MinioException e) 
        {
            System.out.println("Minio Error occurred: " + e.getMessage());
        }
        catch (Exception e) 
        {
            System.out.println("Error occurred: " + e.getMessage());
        }

        return client;
    }
}