package rs.ac.uns.acs.nais.GraphDatabaseService.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.ApiResponseDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.UserCreateRequestDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.UserDTO;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.User;
import rs.ac.uns.acs.nais.GraphDatabaseService.service.IUserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;
    

    /**
    * http://localhost:9003/graph-database-service/api/users
    * */
    @PostMapping
    public ResponseEntity<ApiResponseDTO<UserDTO>> addNewUser(@RequestBody UserCreateRequestDTO userDTO) {
        User userEntity = userDTO.toEntity();
        
        UserDTO created = userService.addNewUser(userEntity);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.success("User created and synced to Vector DB (SAGA Success)",created));
    }
}
