package rs.ac.uns.acs.nais.GraphDatabaseService.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.CategoryStatsDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.UserSegmentDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.SportProduct;

import java.util.List;

@Repository
public interface ComplexQueriesRepository extends Neo4jRepository<SportProduct, Long>, ComplexQueriesCustom {

    // =================================================================
    // 1. COMPLEX QUERY (Analytical): Collaborative Filtering
    // "Users who bought X also bought Y"
    // Criteria: MATCH, WHERE, WITH, COUNT, AGGREGATION
    // =================================================================
    @Query("""
        MATCH (u:User)-[:PURCHASED]->(p:SportProduct)
        WHERE u.userId = $userId
        MATCH (p)<-[:PURCHASED]-(peer:User)-[:PURCHASED]->(rec:SportProduct)
        WHERE NOT (u)-[:PURCHASED]->(rec)
        WITH rec, COUNT(DISTINCT peer) as strength
        ORDER BY strength DESC
        LIMIT $limit
        RETURN rec
    """)
    List<SportProduct> recommendCollaborative(@Param("userId") Long userId, @Param("limit") Integer limit);

    // =================================================================
    // 3. COMPLEX QUERY (Analytical): Cross-Sell via Hierarchy
    // "Find products in the same category family that I haven't bought"
    // Criteria: MATCH (Deep Traversal), WHERE, WITH
    // =================================================================
    @Query("""
        MATCH (u:User)-[:PURCHASED]->(:SportProduct)-[:IN_CATEGORY]->(c:Category)
        WHERE u.userId = $userId
        WITH DISTINCT u, c
        MATCH (rec:SportProduct)-[:IN_CATEGORY]->(c)
        WHERE NOT (u)-[:PURCHASED]->(rec)
        RETURN DISTINCT rec
        LIMIT $limit
    """)
    List<SportProduct> recommendCrossSell(@Param("userId") Long userId, @Param("limit") Integer limit);

    // =================================================================
    // 4. COMPLEX CRUD: Purchase with Stock Management
    // "Create relationship AND update node property in one transaction"
    // =================================================================
    @Query("""
        MATCH (u:User), (p:SportProduct)
        WHERE u.userId = $userId AND id(p) = $productId AND p.stockQuantity >= $quantity
        WITH u, p, (p.price * $quantity) as finalPrice
        CREATE (u)-[r:PURCHASED {
            purchaseDate: datetime(),
            quantity: $quantity,
            totalPrice: finalPrice,
            orderStatus: 'COMPLETED'
        }]->(p)
        SET p.stockQuantity = p.stockQuantity - $quantity
        RETURN count(r) > 0
    """)
    Boolean executeComplexPurchase(@Param("userId") Long userId,
                                @Param("productId") Long productId, 
                                @Param("quantity") Integer quantity);

    // =================================================================
    // 5. COMPLEX CRUD: Review with Average Rating Recalculation
    // "Create relationship AND recalculate aggregate on node"
    // =================================================================
    @Query("""
        MATCH (u:User), (p:SportProduct)
        WHERE u.userId = $userId AND id(p) = $productId
        MERGE (u)-[r:REVIEWED]->(p)
        SET r.rating = $rating, r.comment = $comment, r.reviewDate = datetime()
        WITH p
        MATCH (p)<-[allReviews:REVIEWED]-()
        WITH p, AVG(allReviews.rating) as newAvg
        SET p.rating = newAvg
    """)
    void executeComplexReview(@Param("userId") Long userId, 
                              @Param("productId") Long productId, 
                              @Param("rating") Integer rating,
                              @Param("comment") String comment);
}