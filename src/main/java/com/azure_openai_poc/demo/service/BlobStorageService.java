package com.azure_openai_poc.demo.service;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BlobStorageService {

    @Value("${azure.storage.connection-string}")
    private String connectionString;

    @Value("${azure.storage.container-name}")
    private String containerName;

    public BlobContainerClient getContainer() {

        return new BlobContainerClientBuilder()
                .connectionString(connectionString)
                .containerName(containerName)
                .buildClient();
    }
    public String upload(MultipartFile file)
            throws Exception {

        BlobClient blobClient =
                getContainer().getBlobClient(
                        file.getOriginalFilename()
                );

        blobClient.upload(
                file.getInputStream(),
                file.getSize(),
                true
        );

        return blobClient.getBlobUrl();
    }





    public List<Map<String, String>> listFiles() {

        List<Map<String, String>> files =
                new ArrayList<>();

        for (BlobItem blobItem :
                getContainer().listBlobs()) {

            BlobClient blobClient =
                    getContainer().getBlobClient(
                            blobItem.getName()
                    );

            Map<String, String> map =
                    new HashMap<>();

            map.put("name", blobItem.getName());

            map.put("url",
                    blobClient.getBlobUrl());

            files.add(map);
        }

        return files;
    }

    public byte[] download(String fileName) {

        BlobClient blobClient =
                getContainer().getBlobClient(
                        fileName
                );

        ByteArrayOutputStream stream =
                new ByteArrayOutputStream();

        blobClient.download(stream);

        return stream.toByteArray();
    }
    private String limitText(String text) {

        int maxLength = 12000;

        if(text.length() > maxLength) {

            return text.substring(0, maxLength);
        }

        return text;
    }
}
