package com.example.minio;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;

import io.minio.BucketEventListener;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import io.minio.messages.EventType;
import io.minio.messages.NotificationConfiguration;
import io.minio.messages.QueueConfiguration;
import io.minio.notification.NotificationInfo;

@Repository
@Controller
public class MinioNotification
{
    private MinioClient minioClient;
    
    @Autowired
    private SimpMessagingTemplate template;
    
    @Autowired
    public MinioNotification(MinioClient minioClient)
    {
        this.minioClient = minioClient;
    }

    //Get bucket event notifications
    public String GetNotification(String bucket)
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
    
    //Set event notification for creation of new objects
    public void SetCreateNotification(String bucket)
    {
        try
        {
            NotificationConfiguration notificationConfiguration = minioClient.getBucketNotification(bucket);
            
            //Add a new SQS configuration
            List<QueueConfiguration> queueConfigurationList = notificationConfiguration.queueConfigurationList();
            for (QueueConfiguration qConfig : queueConfigurationList)
            {
                if(qConfig.queue().equals("arn:minio:sqs::1:webhook")) 
                {
                    System.out.println("Queue already exists: " + qConfig.queue());
                    return;
                }
            }
            
            QueueConfiguration queueConfiguration = new QueueConfiguration();
            queueConfiguration.setQueue("arn:minio:sqs::1:webhook");

            List<EventType> eventList = new LinkedList<>();
            eventList.add(EventType.OBJECT_CREATED_ANY);
            queueConfiguration.setEvents(eventList);

            queueConfigurationList.add(queueConfiguration);
            notificationConfiguration.setQueueConfigurationList(queueConfigurationList);

            //Set updated notification configuration
            minioClient.setBucketNotification(bucket, notificationConfiguration);

            //Set event callback for object created event
            SetCreateListener(bucket);
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

    public void SetCreateListener(String bucket)
    {
        try
        {
            class BucketCreateListener implements BucketEventListener 
            {
                //Event callback 
                @Override
                public void updateEvent(NotificationInfo info) 
                {
                    String msg = info.records[0].s3.bucket.name + "/" + info.records[0].s3.object.key + " has been created";
                    System.out.println(msg);
                    
                    NotificationMeta notificationMeta = new NotificationMeta();
                    notificationMeta.setNotificationMessage(msg);

                    template.convertAndSend("/event/create",notificationMeta);
                }
            }
            
            //Set the event callback
            minioClient.listenBucketNotification(bucket, "", "", new String[]{"s3:ObjectCreated:*"}, new BucketCreateListener());

            return;
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
}