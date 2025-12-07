package rs.ac.uns.acs.nais.GraphDatabaseService.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.ApiResponseDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.CategoryStatsDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.UserSegmentDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.SportProduct;
import rs.ac.uns.acs.nais.GraphDatabaseService.service.IGraphService;

import java.util.List;

@RestController
@RequestMapping("/api/graph")
@CrossOrigin(origins = "*") // Allow frontend access
@RequiredArgsConstructor
public class GraphController {

    private final IGraphService graphService;

    // ============================================================================
    // GROUP 1: RECOMMENDATIONS (Complex Queries 1 & 3)
    // ============================================================================

    /**
     * COMPLEX QUERY 1: Collaborative Filtering
     * "Users who bought what you bought, also bought..."
     * URL: GET /api/graph/recommendations/collaborative?userId=1
     */
    @GetMapping("/recommendations/collaborative")
    public ResponseEntity<ApiResponseDTO<List<SportProduct>>> getCollaborativeRecommendations(
            @RequestParam Long userId) {

        List<SportProduct> recommendations = graphService.getRecommendations(userId);
        return ResponseEntity.ok(ApiResponseDTO.success(
                "Collaborative filtering recommendations retrieved.",
                recommendations));
    }

    /**
     * COMPLEX QUERY 3: Cross-Sell Opportunities
     * "Products in the same Category you might like"
     * URL: GET /api/graph/recommendations/cross-sell?userId=1
     */
    @GetMapping("/recommendations/cross-sell")
    public ResponseEntity<ApiResponseDTO<List<SportProduct>>> getCrossSellRecommendations(
            @RequestParam Long userId) {

        List<SportProduct> crossSell = graphService.getCrossSell(userId);
        return ResponseEntity.ok(ApiResponseDTO.success(
                "Cross-sell recommendations retrieved.",
                crossSell));
    }

    // ============================================================================
    // GROUP 2: ANALYTICS (Complex Queries 4 & 5)
    // ============================================================================

    /**
     * COMPLEX QUERY 4: Category Performance
     * Aggregates revenue, avg rating, etc. per category using Custom Implementation.
     * URL: GET /api/graph/analytics/categories?minPurchases=5
     */
    @GetMapping("/analytics/categories")
    public ResponseEntity<ApiResponseDTO<List<CategoryStatsDTO>>> analyzeCategories(
            @RequestParam(defaultValue = "0") Integer minPurchases) {

        List<CategoryStatsDTO> stats = graphService.analyzeCategoryPerformance(minPurchases);
        return ResponseEntity.ok(ApiResponseDTO.success(
                "Category performance analysis completed.",
                stats));
    }

    /**
     * COMPLEX QUERY 5: User Segmentation (General)
     * URL: GET /api/graph/analytics/segments
     */
    @GetMapping("/analytics/segments")
    public ResponseEntity<ApiResponseDTO<List<UserSegmentDTO>>> getUserSegments() {

        List<UserSegmentDTO> segments = graphService.getUserSegments();
        return ResponseEntity.ok(ApiResponseDTO.success(
                "User segmentation analysis retrieved.",
                segments));
    }

    /**
     * COMPLEX QUERY 5 (Variant): User Segmentation by Minimum Spending
     * URL: GET /api/graph/analytics/segments-filtered?minSpent=500
     */
    @GetMapping("/analytics/segments-filtered")
    public ResponseEntity<ApiResponseDTO<List<UserSegmentDTO>>> getSegmentsBySpending(
            @RequestParam(defaultValue = "100.0") Double minSpent) {

        List<UserSegmentDTO> segments = graphService.segmentUsersByMinSpent(minSpent);
        return ResponseEntity.ok(ApiResponseDTO.success(
                "Filtered user segmentation retrieved.",
                segments));
    }

    // ============================================================================
    // GROUP 3: COMPLEX TRANSACTIONS (Complex CRUD 1 & 2)
    // ============================================================================

    /**
     * COMPLEX CRUD 1: Purchase Product
     * Creates a relationship AND updates stock quantity in one transaction.
     * URL: POST /api/graph/actions/buy?userId=1&productId=10&qty=1
     */
    @PostMapping("/actions/buy")
    public ResponseEntity<ApiResponseDTO<String>> buyProduct(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam int qty) {

        try {
            graphService.buyProduct(userId, productId, qty);

            return ResponseEntity.ok(ApiResponseDTO.success(
                    "Product purchased successfully. Stock updated.",
                    null));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseDTO.error(e.getMessage()));
        }
    }
    /**
     * COMPLEX CRUD 2: Review Product
     * Creates a review relationship AND recalculates the product's average rating.
     * URL: POST /api/graph/actions/review?userId=1&productId=10&rating=5&comment=Great
     */
    @PostMapping("/actions/review")
    public ResponseEntity<ApiResponseDTO<String>> reviewProduct(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam int rating,
            @RequestParam String comment) {

        graphService.reviewProduct(userId, productId, rating, comment);
        return ResponseEntity.ok(ApiResponseDTO.success(
                "Review added. Product average rating recalculated.",
                null));
    }
}