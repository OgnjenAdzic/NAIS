package rs.ac.uns.acs.nais.GraphDatabaseService.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node("Brand")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Brand {

    @Id
    @GeneratedValue
    private Long id;

    private String brandName;
    private String description;
    private String country;
    private Boolean premium;

    @Relationship(type = "BELONGS_TO_BRAND", direction = Relationship.Direction.INCOMING)
    private Set<SportProduct> products = new HashSet<>();

    // Custom constructor without relationships
    public Brand(String brandName, String description, String country, Boolean premium) {
        this.brandName = brandName;
        this.description = description;
        this.country = country;
        this.premium = premium;
    }
}