package rs.ac.uns.acs.nais.GraphDatabaseService.repository.NodesCRUD;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends Neo4jRepository<Category, Long> {}