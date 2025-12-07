package rs.ac.uns.acs.nais.GraphDatabaseService.model;

import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node("SportProduct")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SportProduct {

    @Id
    @GeneratedValue
    private Long id;

    private String productName;
    private String description;
    private Double price;
    private String brand;
    private String category; // e.g., "Running", "Gym", "Cycling", "Swimming", "Team Sports"
    private String subCategory; // e.g., "Shoes", "Apparel", "Equipment"
    private Integer stockQuantity;
    private Integer reservedQuantity;
    private String size; // S, M, L, XL or shoe sizes
    private String color;
    private String gender; // Male, Female, Unisex
    private Double rating;

    @Relationship(type = "BELONGS_TO_BRAND", direction = Relationship.Direction.OUTGOING)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Brand brandRelation;

    @Relationship(type = "IN_CATEGORY", direction = Relationship.Direction.OUTGOING)
    private Category categoryRelation;

    @Relationship(type = "VIEWED_BY", direction = Relationship.Direction.INCOMING)
    private Set<User> viewedByUsers = new HashSet<>();

    @Relationship(type = "PURCHASED_BY", direction = Relationship.Direction.INCOMING)
    private Set<User> purchasedByUsers = new HashSet<>();

    @Relationship(type = "IN_CART_OF", direction = Relationship.Direction.INCOMING)
    private Set<User> inCartOfUsers = new HashSet<>();

    @Relationship(type = "REVIEWED_BY", direction = Relationship.Direction.INCOMING)
    private Set<Review> reviews = new HashSet<>();

    @Relationship(type = "FREQUENTLY_BOUGHT_WITH", direction = Relationship.Direction.OUTGOING)
    private Set<SportProduct> frequentlyBoughtWith = new HashSet<>();

    // Custom constructor without relationships
    public SportProduct(String productName, String description, Double price, String brand,
                        String category, String subCategory, Integer stockQuantity,
                        String size, String color, String gender) {
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.brand = brand;
        this.category = category;
        this.subCategory = subCategory;
        this.stockQuantity = stockQuantity;
        this.reservedQuantity = 0;
        this.size = size;
        this.color = color;
        this.gender = gender;
        this.rating = 0.0;
    }

    // Business logic methods
    public Integer getAvailableQuantity() {
        int stock = (this.stockQuantity != null) ? this.stockQuantity : 0;

        // If reservedQuantity is null, treat it as 0
        int reserved = (this.reservedQuantity != null) ? this.reservedQuantity : 0;
        return stock - reserved;
    }

    public boolean reserveStock(Integer quantity) {
        if (getAvailableQuantity() >= quantity) {
            this.reservedQuantity += quantity;
            return true;
        }
        return false;
    }

    public void releaseStock(Integer quantity) {
        this.reservedQuantity = Math.max(0, this.reservedQuantity - quantity);
    }

    public void confirmPurchase(Integer quantity) {
        this.stockQuantity -= quantity;
        this.reservedQuantity -= quantity;
    }
}