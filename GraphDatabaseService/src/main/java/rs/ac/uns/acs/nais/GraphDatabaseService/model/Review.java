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
public class Review {

    @Id
    @GeneratedValue
    private Long id;

    private Integer rating; // 1-5
    private String comment;
    private LocalDateTime reviewDate;
    private Boolean verified; // verified purchase

    @TargetNode
    private SportProduct product;

    // Custom constructor without id
    public Review(SportProduct product, Integer rating, String comment,
                  LocalDateTime reviewDate, Boolean verified) {
        this.product = product;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = reviewDate;
        this.verified = verified;
    }
}