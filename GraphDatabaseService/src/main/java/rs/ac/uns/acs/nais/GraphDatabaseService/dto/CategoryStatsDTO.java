package rs.ac.uns.acs.nais.GraphDatabaseService.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class CategoryStatsDTO {
    // Getters and Setters...
    private String category;
    private Long productCount;
    private Double avgPrice;
    private Long totalPurchases;
    private Double revenue;
    private Double avgRating;
    private Long reviewCount;
    private Double avgRevenuePerPurchase;

    public CategoryStatsDTO() {}

    public CategoryStatsDTO(Map<String, Object> map) {
        this.category = (String) map.get("category");

        // SAFE CONVERSIONS (Prevents NullPointerException)
        this.productCount = safeLong(map.get("productCount"));
        this.avgPrice = safeDouble(map.get("avgPrice"));
        this.totalPurchases = safeLong(map.get("totalPurchases"));
        this.revenue = safeDouble(map.get("revenue"));
        this.avgRating = safeDouble(map.get("avgRating")); // Fixes the NULL rating issue
        this.reviewCount = safeLong(map.get("reviewCount"));
        this.avgRevenuePerPurchase = safeDouble(map.get("avgRevenuePerPurchase"));
    }

    // Helper to handle Nulls or Integer/Long mismatches
    private Long safeLong(Object value) {
        if (value instanceof Number) return ((Number) value).longValue();
        return 0L;
    }

    // Helper to handle Nulls or Integer/Double mismatches
    private Double safeDouble(Object value) {
        if (value instanceof Number) return ((Number) value).doubleValue();
        return 0.0;
    }

}