package com.example.controller;
import java.io.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.model.Transaction;
import com.example.repository.TransactionRepo;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
@RestController
@RequestMapping("/api/pdf")
public class PdfController {
	@Autowired
	TransactionRepo transactionRepo;
	@GetMapping("/invoice/{transactionId}")
	public ResponseEntity<byte[]> generateInvoice(@PathVariable Long transactionId) throws Exception {
	    Transaction transaction = transactionRepo.findById(transactionId)
	            .orElseThrow(() -> new RuntimeException("Transaction not found"));
	    Document document = new Document();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    PdfWriter.getInstance(document, out);
	    document.open();
	    document.add(new Paragraph("INVOICE"));
	    document.add(new Paragraph("----------------------------"));
	    document.add(new Paragraph("User: " + transaction.getUser().getUsername()));
	    document.add(new Paragraph("Policy: " + transaction.getPolicy().getType()));
	    document.add(new Paragraph("Amount: " + transaction.getAmount() + " EUR"));
	    document.add(new Paragraph("----------------------------"));
	    document.close();
	    byte[] pdfBytes = out.toByteArray();
	    HttpHeaders headers = new HttpHeaders();
	    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice_" + transactionId + ".pdf");
	    return ResponseEntity
	            .ok()
	            .headers(headers)
	            .contentType(MediaType.APPLICATION_PDF)
	            .body(pdfBytes);
	}
}
