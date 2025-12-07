package rs.ac.uns.acs.nais.GraphDatabaseService.model;

import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node("Category")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue
    private Long id;

    private String categoryName; // Running, Gym, Cycling, Swimming, Team Sports
    private String description;

    @Relationship(type = "IN_CATEGORY", direction = Relationship.Direction.INCOMING)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<SportProduct> products = new HashSet<>();

    @Relationship(type = "PARENT_CATEGORY", direction = Relationship.Direction.OUTGOING)
    private Category parentCategory;

    @Relationship(type = "PARENT_CATEGORY", direction = Relationship.Direction.INCOMING)
    private Set<Category> subCategories = new HashSet<>();

    // Custom constructor without relationships
    public Category(String categoryName, String description) {
        this.categoryName = categoryName;
        this.description = description;
    }
}