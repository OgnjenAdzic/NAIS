package rs.ac.uns.acs.nais.GraphDatabaseService.repository.NodesCRUD;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.SportProduct;

import java.util.List;
import java.util.Optional;

public interface SportProductRepository extends Neo4jRepository<SportProduct, Long> {
    @Query("MATCH (p:SportProduct) WHERE p.price >= $min AND p.price <= $max RETURN p")
    List<SportProduct> findByPriceRange(@Param("min") double min, @Param("max") double max);
}
