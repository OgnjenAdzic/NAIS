package rs.ac.uns.acs.nais.GraphDatabaseService.repository.impl;

import org.springframework.data.neo4j.core.Neo4jClient;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.CategoryStatsDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.UserSegmentDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.repository.ComplexQueriesCustom;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ComplexQueriesRepositoryImpl implements ComplexQueriesCustom {
    private final Neo4jClient  neo4jClient;

    public ComplexQueriesRepositoryImpl(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }


    @Override
    public List<CategoryStatsDTO> analyzeCategoryPerformance(Integer minPurchases) {
        String query = """
                MATCH (c:Category)<-[:IN_CATEGORY]-(p:SportProduct)
                            OPTIONAL MATCH (u:User)-[pur:PURCHASED]->(p)
                            WITH c, p,
                                 COUNT(pur) as prodPurchases,
                                 SUM(pur.totalPrice) as prodRevenue
                            WITH c.categoryName as category,
                                 COUNT(DISTINCT p) as productCount,
                                 AVG(p.price) as avgPrice,
                                 SUM(prodPurchases) as totalPurchases,
                                 SUM(prodRevenue) as revenue,
                                 coalesce(AVG(CASE WHEN p.rating > 0 THEN p.rating ELSE null END), 0.0) as avgRating,
                                 SUM(count{(p)<-[:REVIEWED]-()}) as reviewCount            
                            WHERE totalPurchases >= 0         
                            RETURN category,
                                   productCount,
                                   avgPrice,
                                   totalPurchases,
                                   revenue,
                                   avgRating,
                                   reviewCount,
                                   CASE WHEN totalPurchases > 0 THEN revenue / totalPurchases ELSE 0.0 END as avgRevenuePerPurchase
                            ORDER BY revenue DESC
        """;
        Collection<Map<String, Object>> rawResults = neo4jClient.query(query)
                .bind(minPurchases).to("$minPurchases")
                .fetch()
                .all();
        return rawResults.stream()
                .map(CategoryStatsDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserSegmentDTO> segmentUsersByMinSpent(Double minSpent) {
        String query = """
                MATCH (u:User)-[p:PURCHASED]->(prod:SportProduct)-[:IN_CATEGORY]->(cat:Category)
                WITH u, 
                     COUNT(DISTINCT prod) as totalProducts,
                     SUM(p.totalPrice) as totalSpent,
                     AVG(p.totalPrice) as avgOrderValue,
                     COLLECT(DISTINCT cat.categoryName) as categories
                WHERE totalSpent > $minSpent
                WITH u, totalProducts, totalSpent, avgOrderValue, categories,
                     CASE 
                        WHEN totalSpent >= 1000 THEN 'Premium'
                        WHEN totalSpent >= 500 THEN 'Gold'
                        WHEN totalSpent >= 200 THEN 'Silver'
                        ELSE 'Bronze'
                     END as customerTier
                RETURN u.userId as userId,
                       u.username as username,
                       totalProducts,
                       totalSpent,
                       avgOrderValue,
                       SIZE(categories) as categoryDiversity,
                       customerTier
                ORDER BY totalSpent DESC
                """;

        Collection<Map<String, Object>> rawResults = neo4jClient.query(query)
                .bind(minSpent != null ? minSpent : 0.0).to("minSpent")
                .fetch()
                .all();

        return rawResults.stream()
                .map(UserSegmentDTO::new)
                .collect(Collectors.toList());
    }
}
