package com.freightfox.pdf.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import com.freightfox.pdf.data.BillData;

@Service
public class Pdfservice {
    private static final String PDF_DIRECTORY = "pdfs/";

    // Method to generate PDF and return as InputStreamResource
    public InputStreamResource generatePdf(BillData request) throws IOException {
       
        if (request == null) {
            throw new IllegalArgumentException("BillData cannot be null");
        }
    	String fileName = request.getSeller() + "_" + request.getBuyer() + ".pdf";
        String filePath = PDF_DIRECTORY + fileName;

        
        // Check if PDF already exists
        if (Files.exists(Paths.get(filePath))) {
          
            InputStream inputStream = new ByteArrayInputStream(Files.readAllBytes(Paths.get(filePath)));
            return new InputStreamResource(inputStream);
        }

        // Create PDF
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PDDocument document = new PDDocument()) {
             
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 700);

                // Add content to PDF
                contentStream.showText("Seller: " + request.getSeller());
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("GSTIN: " + request.getSellerGstin());
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Address: " + request.getSellerAddress());
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Buyer: " + request.getBuyer());
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("GSTIN: " + request.getBuyerGstin());
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Address: " + request.getBuyerAddress());
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Items:");

                for (BillData.Item item : request.getItems()) {
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText(item.getName() + " - " + item.getQuantity() + " - " + item.getRate() + " - " + item.getAmount());
                }

                contentStream.endText();
            }

            
            document.save(outputStream);
            InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            return new InputStreamResource(inputStream);
        }
    }
}