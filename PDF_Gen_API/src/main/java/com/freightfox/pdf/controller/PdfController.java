package com.freightfox.pdf.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freightfox.pdf.data.BillData;
import com.freightfox.pdf.service.Pdfservice;

@RestController
@RequestMapping("/bill")
public class PdfController {
	
	@Autowired
	private final Pdfservice pdfService;

    public PdfController(Pdfservice pdfService) {
        this.pdfService = pdfService;
    }

    @PostMapping("/pdf")
    public ResponseEntity<InputStreamResource> generatePdf(@RequestBody BillData request) {
        try {
            InputStreamResource resource = pdfService.generatePdf(request);
            HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + request.getSeller() + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
  }
}


