package com.mycicd.deploy.awss3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class S3Downloader {
    private static final int THREAD_POOL_SIZE = 10;

    public static void main(String[] args) {
        String bucketName = "mysql-backup0-my-v1";
        String key = "Untitled.rtf";
        String fileName = "Downloaded.rtf";

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().build();
        S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketName, key));

        long contentLength = s3Object.getObjectMetadata().getContentLength();
        long byteRangeSize = contentLength / THREAD_POOL_SIZE;

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        for (int i = 0; i < THREAD_POOL_SIZE; i++) {
            long start = i * byteRangeSize;
            long end = (i == THREAD_POOL_SIZE - 1) ? contentLength - 1 : start + byteRangeSize - 1;

            executor.execute(() -> {
                try {
                    System.out.println("******************************************************************************************"+Thread.currentThread().getName());
                    downloadRange(s3Client, bucketName, key, start, end, fileName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
    }

    private static void downloadRange(AmazonS3 s3Client, String bucketName, String key, long start, long end, String fileName) throws Exception {
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
        getObjectRequest.setRange(start, end);

        S3Object s3Object = s3Client.getObject(getObjectRequest);
        InputStream inputStream = s3Object.getObjectContent();

        FileOutputStream fileOutputStream = new FileOutputStream(fileName, true);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            bufferedOutputStream.write(buffer, 0, bytesRead);
        }

        inputStream.close();
        bufferedOutputStream.close();
    }
}
