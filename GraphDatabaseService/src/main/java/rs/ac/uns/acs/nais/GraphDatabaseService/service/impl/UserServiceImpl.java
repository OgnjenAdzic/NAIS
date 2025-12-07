package rs.ac.uns.acs.nais.GraphDatabaseService.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.UserDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.VectorUserDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.User;
import rs.ac.uns.acs.nais.GraphDatabaseService.repository.NodesCRUD.UserRepository;
import rs.ac.uns.acs.nais.GraphDatabaseService.service.IUserService;
import rs.ac.uns.acs.nais.GraphDatabaseService.service.VectorClient;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final VectorClient vectorClient;

    @Override
    @Transactional
    public UserDTO addNewUser(User user){
        long stableId = (long) (Math.random() * 100000) + 1000;
        user.setUserId(stableId);

        User savedUser = userRepository.save(user);
        System.out.println(">>> SAGA step 1: saved to neo4j");

        try {
            VectorUserDTO vectorUserDTO = new VectorUserDTO(
                    stableId,
                    savedUser.getUsername(),
                    savedUser.getPreferredSport(),
                    savedUser.getFitnessLevel()
            );

            vectorClient.syncUser(vectorUserDTO);
            System.out.println(">>> SAGA STEP 2: Synced to Milvus.");

        } catch (Exception e) {
            System.err.println("!!! SAGA FAILURE: " + e.getMessage());
            System.err.println("!!! ROLLING BACK NEO4J...");

            userRepository.deleteById(savedUser.getId());

            throw new RuntimeException("Transaction Failed. User creation rolled back.");
        }

        return convertToDTO(savedUser);
    }

    private UserDTO convertToDTO(User user) {
        if (user == null) {
            return null;
        }

        return new UserDTO(
                user.getUserId(),       // IMPORTANT: Map the Stable Public ID, not user.getId()
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPreferredSport(),
                user.getFitnessLevel()
        );
    }
}
