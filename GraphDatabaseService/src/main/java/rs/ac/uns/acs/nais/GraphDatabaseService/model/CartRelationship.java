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
public class CartRelationship {

    @Id
    @GeneratedValue
    private Long id;

    private LocalDateTime addedAt;
    private Integer quantity;

    @TargetNode
    private SportProduct product;

    // Custom constructor without id
    public CartRelationship(SportProduct product, LocalDateTime addedAt, Integer quantity) {
        this.product = product;
        this.addedAt = addedAt;
        this.quantity = quantity;
    }
}