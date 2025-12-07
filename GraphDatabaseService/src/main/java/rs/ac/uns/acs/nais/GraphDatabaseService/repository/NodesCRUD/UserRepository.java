package rs.ac.uns.acs.nais.GraphDatabaseService.repository.NodesCRUD;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.User;

import java.util.Optional;

public interface UserRepository extends Neo4jRepository<User, Long> {
    Optional<User> findByUserId(Long userId);
}
