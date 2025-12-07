package rs.ac.uns.acs.nais.GraphDatabaseService.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.SportProduct;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.User;
import rs.ac.uns.acs.nais.GraphDatabaseService.repository.ComplexQueriesRepository;
import rs.ac.uns.acs.nais.GraphDatabaseService.repository.NodesCRUD.SportProductRepository;
import rs.ac.uns.acs.nais.GraphDatabaseService.repository.NodesCRUD.UserRepository;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final UserRepository userRepository;
    private final SportProductRepository productRepository;
    private final ComplexQueriesRepository complexRepository;

    @Transactional(readOnly = true)
    public byte[] generateReport(Long targetUserId, Double minPrice, Double maxPrice) throws DocumentException, IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);
        document.open();

        // Title
        addSectionTitle(document, "ActiveFit System Report", Color.BLUE, 18);
        document.add(new Paragraph("Generated for User ID: " + targetUserId));
        document.add(new Paragraph("Date: " + java.time.LocalDate.now()));
        document.add(new Paragraph(" "));

        // ====================================================================
        // SIMPLE SECTION 1: Registered Users
        // ====================================================================
        addSectionTitle(document, "1. Registered Users Overview", Color.DARK_GRAY, 14);

        List<User> users = userRepository.findAll();
        PdfPTable userTable = new PdfPTable(3);
        userTable.setWidthPercentage(100);
        addTableHeader(userTable, "ID", "Username", "Preferred Sport");

        for (User u : users) {
            userTable.addCell(String.valueOf(u.getUserId()));
            userTable.addCell(u.getUsername());
            userTable.addCell(u.getPreferredSport());
        }
        document.add(userTable);
        document.add(new Paragraph(" "));

        // ====================================================================
        // SIMPLE SECTION 2: Products in Price Range
        // ====================================================================
        addSectionTitle(document, "2. Price Catalog ($" + minPrice + " - $" + maxPrice + ")", Color.DARK_GRAY, 14);

        List<SportProduct> rangeProducts = productRepository.findByPriceRange(minPrice, maxPrice);

        if (rangeProducts.isEmpty()) {
            document.add(new Paragraph("No products found in this range."));
        } else {
            PdfPTable priceTable = new PdfPTable(4);
            priceTable.setWidthPercentage(100);
            addTableHeader(priceTable, "Product", "Brand", "Category", "Price");

            for (SportProduct p : rangeProducts) {
                priceTable.addCell(p.getProductName());
                priceTable.addCell(p.getBrand());
                priceTable.addCell(p.getCategory());

                PdfPCell priceCell = new PdfPCell(new Phrase("$" + p.getPrice()));
                priceCell.setBackgroundColor(Color.YELLOW);
                priceTable.addCell(priceCell);
            }
            document.add(priceTable);
        }
        document.add(new Paragraph(" "));

        // ====================================================================
        // COMPLEX SECTION 1: Collaborative Recommendations
        // ====================================================================
        addSectionTitle(document, "3. Personalized Recommendations (Collaborative)", Color.RED, 14);
        document.add(new Paragraph("Logic: Products bought by users with similar purchase history (Triadic Closure)."));
        document.add(new Paragraph(" "));

        // Reuse the existing Complex Query!
        List<SportProduct> recommendations = complexRepository.recommendCollaborative(targetUserId, 5);

        if (recommendations.isEmpty()) {
            document.add(new Paragraph("No collaborative recommendations available (User needs purchase history)."));
        } else {
            PdfPTable recTable = new PdfPTable(3);
            recTable.setWidthPercentage(100);
            addTableHeader(recTable, "Recommended Item", "Category", "Why?");

            for (SportProduct p : recommendations) {
                recTable.addCell(p.getProductName());
                recTable.addCell(p.getCategory());

                PdfPCell reasonCell = new PdfPCell(new Phrase("Popular among similar users"));
                reasonCell.setBackgroundColor(Color.GREEN);
                recTable.addCell(reasonCell);
            }
            document.add(recTable);
        }

        document.close();
        return out.toByteArray();
    }

    // --- Helpers ---
    private void addSectionTitle(Document doc, String text, Color color, int size) throws DocumentException {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, size, color);
        Paragraph p = new Paragraph(text, font);
        p.setSpacingAfter(10);
        doc.add(p);
    }

    private void addTableHeader(PdfPTable table, String... headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header));
            cell.setBackgroundColor(Color.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }
}