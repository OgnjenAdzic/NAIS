package rs.ac.uns.acs.nais.GraphDatabaseService.service;

import rs.ac.uns.acs.nais.GraphDatabaseService.dto.UserDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.User;

public interface IUserService {
    UserDTO addNewUser(User user);
}
