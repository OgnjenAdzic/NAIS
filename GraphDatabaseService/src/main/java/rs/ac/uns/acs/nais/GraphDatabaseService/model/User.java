package rs.ac.uns.acs.nais.GraphDatabaseService.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node("User")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue
    private Long id;
    private Long userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String preferredSport; // Running, Gym, Cycling, etc.
    private String fitnessLevel; // Beginner, Intermediate, Advanced

    @Relationship(type = "VIEWED", direction = Relationship.Direction.OUTGOING)
    private Set<ViewRelationship> viewedProducts = new HashSet<>();

    @Relationship(type = "PURCHASED", direction = Relationship.Direction.OUTGOING)
    private Set<PurchaseRelationship> purchasedProducts = new HashSet<>();

    @Relationship(type = "ADDED_TO_CART", direction = Relationship.Direction.OUTGOING)
    private Set<CartRelationship> cartItems = new HashSet<>();

    @Relationship(type = "REVIEWED", direction = Relationship.Direction.OUTGOING)
    private Set<Review> reviews = new HashSet<>();

    @Relationship(type = "SIMILAR_TO", direction = Relationship.Direction.OUTGOING)
    private Set<User> similarUsers = new HashSet<>();

    public User(Long userId, String username, String email, String firstName, String lastName,
                String preferredSport, String fitnessLevel) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.preferredSport = preferredSport;
        this.fitnessLevel = fitnessLevel;
    }
}
