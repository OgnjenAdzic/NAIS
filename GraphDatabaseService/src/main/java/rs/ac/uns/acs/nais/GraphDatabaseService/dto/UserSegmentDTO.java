package rs.ac.uns.acs.nais.GraphDatabaseService.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;


@Getter
@Setter
@NoArgsConstructor
public class UserSegmentDTO {
    private Long id;
    private String username;
    private Long totalProducts;
    private Double totalSpent;
    private Double avgOrderValue;
    private Integer categoryDiversity;
    private String customerTier;

    public UserSegmentDTO(Map<String, Object> map) {
        this.id = safeLong(map.get("userId"));
        this.username = (String) map.get("username");
        this.customerTier = (String) map.get("customerTier");

        // Use helper methods to safely convert Neo4j numbers to Java types
        this.totalProducts = safeLong(map.get("totalProducts"));
        this.totalSpent = safeDouble(map.get("totalSpent"));
        this.avgOrderValue = safeDouble(map.get("avgOrderValue"));

        // FIX: Neo4j returns SIZE() as Long, so we must convert to Integer safely
        this.categoryDiversity = safeInt(map.get("categoryDiversity"));
    }

    // --- HELPER METHODS (Copy these exactly) ---

    private Long safeLong(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return 0L;
    }

    private Double safeDouble(Object value) {
        if (value instanceof Number) {
            double d = ((Number) value).doubleValue();
            if (Double.isNaN(d) || Double.isInfinite(d)) return 0.0;
            return d;
        }
        return 0.0;
    }

    private Integer safeInt(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue(); // Converts Long/Double to Integer
        }
        return 0;
    }
}