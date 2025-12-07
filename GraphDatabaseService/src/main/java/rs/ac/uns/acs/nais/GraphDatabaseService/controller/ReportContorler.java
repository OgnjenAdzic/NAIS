package rs.ac.uns.acs.nais.GraphDatabaseService.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.uns.acs.nais.GraphDatabaseService.service.ReportService;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportContorler {
    private final ReportService reportService;

    @GetMapping("/generate")
    public ResponseEntity<byte[]> generateReport(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") Double minPrice,
            @RequestParam(defaultValue = "1000") Double maxPrice) {
        try {
            byte[] pdfBytes = reportService.generateReport(userId, minPrice, maxPrice);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=activefit_report.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
