package rs.ac.uns.acs.nais.GraphDatabaseService.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.LocalDateTime;

@RelationshipProperties
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRelationship {

    @Id
    @GeneratedValue
    private Long id;

    private LocalDateTime purchaseDate;
    private Integer quantity;
    private Double totalPrice;
    private String orderStatus; // PENDING, COMPLETED, CANCELLED

    @TargetNode
    private SportProduct product;

    // Custom constructor without id
    public PurchaseRelationship(SportProduct product, LocalDateTime purchaseDate,
                                Integer quantity, Double totalPrice, String orderStatus) {
        this.product = product;
        this.purchaseDate = purchaseDate;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.orderStatus = orderStatus;
    }
}