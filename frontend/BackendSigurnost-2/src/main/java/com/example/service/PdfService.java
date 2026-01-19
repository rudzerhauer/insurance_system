package com.example.service;
import java.io.ByteArrayOutputStream;
import org.springframework.stereotype.Service;
import com.example.model.Transaction;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
@Service
public class PdfService {
    public byte[] generateInvoicePdf(Transaction t) throws Exception {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();
        document.add(new Paragraph("===== INSURANCE INVOICE ====="));
        document.add(new Paragraph("Transaction ID: " + t.getId()));
        document.add(new Paragraph("Date: " + t.getCreatedAt()));
        document.add(new Paragraph("Customer: " + t.getUser().getUsername()));
        document.add(new Paragraph("Email: " + t.getUser().getEmail()));
        document.add(new Paragraph("Policy: " + t.getPolicy().getType().getName()));
        document.add(new Paragraph("Price: " + t.getAmount() + " EUR"));
        document.add(new Paragraph("Provider: " + t.getProvider()));
        document.add(new Paragraph("Status: " + t.getSuccess()));
        document.close();
        return out.toByteArray();
    }
}
