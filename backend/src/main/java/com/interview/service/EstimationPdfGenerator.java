package com.interview.service;

import com.interview.dto.estimation.EstimationInfo;
import com.interview.dto.workitem.WorkItemDto;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Slf4j
@Service
public class EstimationPdfGenerator {

    public InputStream generateEstimatePdf(EstimationInfo estimationInfo) {
        log.info("Generation pdf for vin: {}", estimationInfo.carVin());
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("Car Estimation Report")
                    .setFontSize(18));
            document.add(new Paragraph("Car VIN: " + estimationInfo.carVin())
                    .setFontSize(12));
            document.add(new Paragraph("\n"));

            Table table = new Table(new float[]{50f, 150f, 250f, 80f});

            table.addHeaderCell(new Cell().add(new Paragraph("ID")));
            table.addHeaderCell(new Cell().add(new Paragraph("Name")));
            table.addHeaderCell(new Cell().add(new Paragraph("Description")));
            table.addHeaderCell(new Cell().add(new Paragraph("Price")));

            float total = 0;
            for (WorkItemDto item : estimationInfo.workItems()) {
                table.addCell(String.valueOf(item.id()));
                table.addCell(item.name());
                table.addCell(item.description());
                table.addCell(String.format("$%.2f", item.price()));
                total += item.price();
            }

            document.add(table);

            document.add(new Paragraph("\n"));
            document.add(new Paragraph(String.format("Total: $%.2f", total))
                    .setFontSize(14));

            document.close();

            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (Exception e) {
            log.error("Failed to generate pdf for vin: {}", estimationInfo.carVin());
            throw new RuntimeException("Error generating PDF", e);
        }
    }
}
