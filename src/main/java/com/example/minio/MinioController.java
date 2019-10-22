package com.example.minio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/retrieve")
public class MinioController
{
    private MinioDao minioDao;

    @Autowired
    public MinioController(MinioDao minioDao) 
    {
        this.minioDao = minioDao;
    }

    @GetMapping("/getBuckets")
    public String GetBuckets()
    {
        String buckets = minioDao.GetBuckets();
        return buckets;
    }

    @PostMapping("/postObject")
    public void postObject(@RequestParam(value="bucket") String bucket,
                            @RequestParam(value="objectName") String objectName,
                            @RequestParam(value="fileName") String fileName,
                            @RequestParam(value="contentType") String contentType) 
    {
        minioDao.PutObjectToBucket(bucket, objectName, fileName, contentType);
    }

    @GetMapping("/getNotification")
    @ResponseBody
    public String GetNotification(@RequestParam("bucket") String bucket)
    {
        String notification = minioDao.GetBucketNotification(bucket);
        return notification;
    }

    @GetMapping("/setCreateNotification")
    @ResponseBody
    public void SetCreateNotification(@RequestParam("bucket") String bucket)
    {
        minioDao.SetBucketCreateNotification(bucket);
    }

    @GetMapping("/listObjects")
    @ResponseBody
    public String ListObjects(@RequestParam("bucket") String bucket)
    {
        String objects = "";
        List<ObjectMeta> objs = minioDao.ListObjectsInBucket(bucket);

        for(ObjectMeta obj : objs)
        {
            objects += "Name: ";
            objects += obj.getName();
            objects += '\n';
            objects += "Type: ";
            objects += obj.getType();
            objects += '\n';
            objects += "Size: ";
            objects += obj.getSize();
            objects += '\n';
            objects += "Created: ";
            objects += obj.getCreated();
            objects += "\n\n";
        }

        return objects;
    } 

    @GetMapping("/getObjectStat")
    @ResponseBody
    public String GetObjectStat(@RequestParam("bucket") String bucket, @RequestParam("name") String objectName) 
    {
        String stats = "";
        ObjectMeta obj = minioDao.GetObjectStat(bucket, objectName);

        stats += "Name: ";
        stats += obj.getName();
        stats += '\n';
        stats += "Type: ";
        stats += obj.getType();
        stats += '\n';
        stats += "Size: ";
        stats += obj.getSize();
        stats += '\n';
        stats += "Created: ";
        stats += obj.getCreated();
        stats += '\n';

        return stats;
    }
}