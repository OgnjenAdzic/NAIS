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
public class ViewRelationship {

    @Id
    @GeneratedValue
    private Long id;

    private LocalDateTime viewedAt;
    private Integer viewDuration; // in seconds

    @TargetNode
    private SportProduct product;

    // Custom constructor without id
    public ViewRelationship(SportProduct product, LocalDateTime viewedAt, Integer viewDuration) {
        this.product = product;
        this.viewedAt = viewedAt;
        this.viewDuration = viewDuration;
    }
}