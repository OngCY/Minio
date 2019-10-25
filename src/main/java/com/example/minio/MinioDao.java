package com.example.minio;

import java.util.ArrayList;
import java.util.List;

//import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.Result;
import io.minio.errors.MinioException;
import io.minio.messages.Bucket;
import io.minio.messages.Item;

@Repository
public class MinioDao
{
    private MinioClient minioClient;
    //private ObjectMapper objectMapper;
    
    @Autowired
    public MinioDao(MinioClient minioClient) 
    {
        //this.objectMapper = objectMapper;
        this.minioClient = minioClient;
    }

    //Upload object to bucket
    public void PutObject(String bucket, String objectName, String fileName, String contentType)
    {
        try 
        {
            minioClient.putObject(bucket, objectName, fileName ,"application/octet-stream");
        } 
        catch (MinioException e) 
        {
            System.out.println("Minio Error occurred: " + e.toString());
        }
        catch(Exception e)
        {
            System.out.println("Error occurred: " + e.toString());
        }
    }

    //Retrieve objects' metadata from a bucket
    public List<ObjectMeta> ListObjectsInBucket(String bucket)
    {
        List<ObjectMeta> resultList = new ArrayList<>();
        
        try 
        {
            //Check whether the bucket exists or not.
            boolean found = minioClient.bucketExists(bucket);
            if (found) 
            {
                Iterable<Result<Item>> myObjects = minioClient.listObjects(bucket);
                for (Result<Item> result : myObjects) 
                {
                    Item item = result.get();
                    resultList.add(GetObjectStat(bucket,item.objectName()));
                }
            } 
            else 
                System.out.println("mybucket does not exist");
        }
        catch (MinioException e) 
        {
            System.out.println("Minio Error occurred: " + e.toString());
        }
        catch(Exception e)
        {
            System.out.println("Error occurred: " + e.toString());
        }

        return resultList;
    } 

    //Get the metadata of an object (bucket name, filename, content type, created time, size)
    public ObjectMeta GetObjectStat(String bucket, String objectName)
    {
        ObjectMeta objectMeta = new ObjectMeta();

        try 
        {
            ObjectStat objectStat = minioClient.statObject(bucket, objectName);            
            
            objectMeta.setName(objectStat.name());
            objectMeta.setType(objectStat.contentType());
            objectMeta.setSize(Long.toString(objectStat.length()));
            objectMeta.setCreated(objectStat.createdTime().toString());
        }
        catch(MinioException e) 
        {
            System.out.println("Minio Error occurred: " + e.toString());
        }
        catch(Exception e)
        {
            System.out.println("Error occured: " + e.toString());
        }

        return objectMeta;
    }

    //Get a list of buckets
    public String GetBuckets()
    {
        String buckets = "";
        
        try
        {
            List<Bucket> bucketList = minioClient.listBuckets();
            for (Bucket bucket : bucketList) 
            {
                buckets += bucket.name();
                buckets += "\n";    
            }
        }
        catch (MinioException e) 
        {
            System.out.println("Minio Error occurred: " + e.getMessage());
        }
        catch (Exception e) 
        {
            System.out.println("Error occurred: " + e.getMessage());
        }

        return buckets;
    }
}