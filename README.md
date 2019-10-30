# Minio Endpoints

Download object to a local drive
- URL: /retrieve/downloadObject
- Method: GET
- Header: none
- Cookie: none
- URL params:
    - bucket [string]
    - objectName [string]
    - location [string]
- Body params: none
- Example: 
    - http://localhost:8082/retrieve/downloadObject?bucket=test&objectName=kyiv.pdf&location=C:\\minio\\downloads\\kyiv.pdf
    Downloads the pdf file from the test bucket into C:\minio\downloads
- Note: none

Upload object from a local drive
- URL: /retrieve/putObject
- Method: POST
- Header: none
- Cookie: none
- URL params: none
- Body params:
    - bucket [string]
    - objectName [string]
    - filename [string]
    - contentType [string]
- Example: http://localhost:8082/retrieve/postObject
- Note: none

Get notifications from a Minio bucket
- URL: /retrieve/getNotification
- Method: GET
- Header: none
- Cookie: none
- URL params: 
    - bucket [string]
- Body params: none
- Example: http://localhost:8082/retrieve/setNotificationListener?bucket=test
- Note: none

Create a bucket event notification for the create event 
- URL: /retrieve/setCreateNotification
- Method: POST
- Header: none
- Cookie: none
- URL params: none
- Body params:
    - bucket [string]
- Example: http://localhost:8082/retrieve/setCreateNotification
- Note: Needs to be called only once for a bucket

Set the callback method for the create event notification 
- URL: /retrieve/setNotificationListener
- Method: POST
- Header: none
- Cookie: none
- URL params: none
- Body params:
    - bucket [string]
- Example: http://localhost:8082/retrieve/setNotificationListener
- Note: Needs to run this endpoint each time the program starts for the bucket

Get a list of object metadata from a bucket
- URL: /retrieve/listObjects
- Method: GET
- Header: none
- Cookie: none
- URL params: 
    - bucket [string]
- Body params: none
- Example: http://localhost:8082/retrieve/listObjects?bucket=test
- Note: Returns a string containing the name, size, type and create date of the objects

Get an object's metadata from a bucket
- URL: /retrieve/getObjectStat
- Method: GET
- Header: none
- Cookie: none
- URL params: 
    - bucket [string]
    - name [string]
- Body params: none
- Example: http://localhost:8082/retrieve/getObjectStat?bucket=test&name=gitlab token.txt
- Note: Returns a string containing the name, size, type and create date of the object

Get a list of buckets from Minio
- URL: /retrieve/getBuckets
- Method: GET
- Header: none
- Cookie: none
- URL params: 
    - bucket [string]
- Body params: none
- Example: http://localhost:8082/retrieve/getBuckets
- Note: none