package com.example.minio;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

//import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.minio.BucketEventListener;
import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.Result;
import io.minio.errors.MinioException;
import io.minio.messages.Bucket;
import io.minio.messages.EventType;
import io.minio.messages.Item;
import io.minio.messages.NotificationConfiguration;
import io.minio.messages.QueueConfiguration;
import io.minio.notification.NotificationInfo;

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

    public void PutObjectToBucket(String bucket, String objectName, String fileName, String contentType)
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

    public String GetBucketNotification(String bucket)
    {
        String notifications = "";

        try
        {
            NotificationConfiguration notificationConfiguration = minioClient.getBucketNotification(bucket);
            notifications = notificationConfiguration.toString();
        }
        catch (MinioException e) 
        {
            System.out.println("Minio Error occurred: " + e.toString());
        }
        catch(Exception e)
        {
            System.out.println("Error occurred: " + e.toString());
        }

        return notifications;
    }
    
    public void SetBucketCreateNotification(String bucket)
    {
        try
        {
            NotificationConfiguration notificationConfiguration = minioClient.getBucketNotification(bucket);

            //Add a new SQS configuration.
            List<QueueConfiguration> queueConfigurationList = notificationConfiguration.queueConfigurationList();
            QueueConfiguration queueConfiguration = new QueueConfiguration();
            queueConfiguration.setQueue("arn:minio:sqs::1:webhook");

            List<EventType> eventList = new LinkedList<>();
            eventList.add(EventType.OBJECT_CREATED_ANY);
            queueConfiguration.setEvents(eventList);

            //Filter filter = new Filter();
            //filter.setPrefixRule("images");
            //filter.setSuffixRule("pg");
            //queueConfiguration.setFilter(filter);

            queueConfigurationList.add(queueConfiguration);
            notificationConfiguration.setQueueConfigurationList(queueConfigurationList);

            //Set updated notification configuration
            minioClient.setBucketNotification(bucket, notificationConfiguration);

            //Set create object event listener
            minioClient.listenBucketNotification(bucket, "", "",
            new String[]{"s3:ObjectCreated:*"}, new TestBucketListener());
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

    //List objects' metadata from a bucket
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

    //Get the metadata of the object (bucket name, filename, content type, created time, size)
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
    class TestBucketListener implements BucketEventListener 
    {
        @Override
        public void updateEvent(NotificationInfo info) 
        {
          System.out.println(info.records[0].s3.bucket.name + "/"
              + info.records[0].s3.object.key + " has been created");
        }
    }
}