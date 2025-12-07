package rs.ac.uns.acs.nais.GraphDatabaseService.repository.NodesCRUD;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Brand;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface BrandRepository extends Neo4jRepository<Brand, Long> {}