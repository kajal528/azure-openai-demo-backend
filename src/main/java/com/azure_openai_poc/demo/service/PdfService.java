package com.azure_openai_poc.demo.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

@Service
public class PdfService {

    public String extract(byte[] bytes)
            throws Exception {

        PDDocument document =
                PDDocument.load(bytes);

        PDFTextStripper stripper =
                new PDFTextStripper();

        String text =
                stripper.getText(document);

        document.close();

        return text;
    }
}