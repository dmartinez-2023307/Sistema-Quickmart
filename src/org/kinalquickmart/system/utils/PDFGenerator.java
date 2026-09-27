package org.kinalquickmart.system.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.kinalquickmart.system.model.ProductoTicket;
import java.io.FileOutputStream;
import java.util.List;

public class PDFGenerator {

    public static String generarFacturaConPago(
            String numeroFactura,
            String fecha,
            String cajero,
            String nit,
            List<ProductoTicket> productos,
            double subtotal,
            double iva,
            double total,
            String formaPago,
            double recibido,
            double cambio
    ) {
        String rutaPDF = "facturas/Factura_" + numeroFactura + ".pdf";

        try {
            new java.io.File("facturas").mkdirs();
            Document document = new Document(PageSize.A5);
            PdfWriter.getInstance(document, new FileOutputStream(rutaPDF));
            document.open();

            // ENCABEZADO
            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new BaseColor(26, 82, 118));
            Paragraph titulo = new Paragraph("QUICKMART", fontTitle);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            document.add(new Paragraph("FACTURA ELECTRÓNICA"));
            document.add(new Paragraph("RUC: 1234567890001"));
            
            if (nit != null && !nit.isEmpty()) {
                document.add(new Paragraph("NIT/CF: " + nit));
            }
            
            document.add(new Paragraph(" "));

            // DATOS DE LA FACTURA
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{1, 2});

            infoTable.addCell("Factura #:");
            infoTable.addCell(numeroFactura);
            infoTable.addCell("Fecha:");
            infoTable.addCell(fecha);
            infoTable.addCell("Cajero:");
            infoTable.addCell(cajero);

            document.add(infoTable);
            document.add(new Paragraph(" "));

            // TABLA DE PRODUCTOS
            PdfPTable productosTable = new PdfPTable(4);
            productosTable.setWidthPercentage(100);
            productosTable.setWidths(new float[]{0.8f, 2.5f, 1, 1.2f});

            Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            productosTable.addCell(new Phrase("Cant", fontBold));
            productosTable.addCell(new Phrase("Descripción", fontBold));
            productosTable.addCell(new Phrase("P.Unit", fontBold));
            productosTable.addCell(new Phrase("Total", fontBold));

            Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA);
            for (ProductoTicket prod : productos) {
                productosTable.addCell(String.valueOf(prod.getCantidad()));
                productosTable.addCell(prod.getNombre());
                productosTable.addCell("Q" + String.format("%.2f", prod.getPrecioUnitario()));
                productosTable.addCell("Q" + String.format("%.2f", prod.getTotal()));
            }

            document.add(productosTable);
            document.add(new Paragraph(" "));

            // TOTALES
            PdfPTable totalesTable = new PdfPTable(2);
            totalesTable.setWidthPercentage(50);
            totalesTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalesTable.setWidths(new float[]{2, 1});

            totalesTable.addCell("Subtotal:");
            totalesTable.addCell("Q" + String.format("%.2f", subtotal));
            totalesTable.addCell("IVA (15%):");
            totalesTable.addCell("Q" + String.format("%.2f", iva));
            totalesTable.addCell("TOTAL:");
            totalesTable.addCell(new Phrase("Q" + String.format("%.2f", total), fontBold));

            document.add(totalesTable);
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Forma de pago: " + formaPago));
            document.add(new Paragraph("Recibido: Q" + String.format("%.2f", recibido)));
            document.add(new Paragraph("Cambio: Q" + String.format("%.2f", cambio)));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("¡Gracias por su compra!"));

            document.close();
            return rutaPDF;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}