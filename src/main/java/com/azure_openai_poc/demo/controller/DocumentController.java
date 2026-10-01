package com.azure_openai_poc.demo.controller;

import com.azure_openai_poc.demo.service.BlobStorageService;
import com.azure_openai_poc.demo.service.GPTService;
import com.azure_openai_poc.demo.service.PdfService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private BlobStorageService blobService;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private GPTService gptService;

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Map<String, String> upload(
            @RequestParam("file") MultipartFile file
    ) {

        try {

            // Upload file to Azure Blob Storage
            String url =
                    blobService.upload(file);

            // Extract PDF text
            String text =
                    pdfService.extract(
                            file.getBytes()
                    );

            // Generate AI summary
            String summary =
                    gptService.summarize(text);

            // Response
            Map<String, String> response =
                    new HashMap<>();

            response.put("url", url);

            response.put("summary", summary);

            response.put(
                    "fileName",
                    file.getOriginalFilename()
            );

            return response;

        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException(
                    "Failed to upload document: "
                            + e.getMessage()
            );
        }
    }

    @GetMapping
    public List<Map<String, String>> files() {

        return blobService.listFiles();
    }

    @PostMapping("/ask")
    public String ask(
            @RequestParam String fileName,
            @RequestParam String question
    ) {

        try {

            // Download file from blob storage
            byte[] file =
                    blobService.download(fileName);

            // Extract text from PDF
            String text =
                    pdfService.extract(file);

            // Ask AI
            return gptService.askQuestion(
                    text,
                    question
            );

        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException(
                    "Failed to process AI question: "
                            + e.getMessage()
            );
        }
    }
}