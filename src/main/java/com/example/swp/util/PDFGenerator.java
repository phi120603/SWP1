package com.example.swp.util;

import com.example.swp.entity.EContract;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;

public class PDFGenerator {

    public static String generateContractPdf(EContract contract) {
        try {
            String fileName = "contract_" + contract.getId() + ".pdf";
            String filePath = "contracts/" + fileName;

            Document document = new Document();
            OutputStream out = new FileOutputStream(filePath);
            PdfWriter.getInstance(document, out);

            document.open();
            document.add(new Paragraph("HỢP ĐỒNG: " + contract.getTitle(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
            document.add(new Paragraph("Ngày tạo: " + contract.getCreatedDate().format(DateTimeFormatter.ISO_DATE)));
            document.add(new Paragraph("Thời gian hiệu lực: " + contract.getStartDate() + " - " + contract.getEndDate()));
            document.add(new Paragraph("Khách hàng: " + contract.getCustomer().getFullname()));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Nội dung hợp đồng:"));
            document.add(new Paragraph(contract.getTerms()));

            document.close();
            out.close();

            return filePath;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
