package ken.vivid.application.service.auth;

import ken.vivid.application.port.input.auth.GetCurrentUserUseCase;
import ken.vivid.application.port.input.auth.saveUser.LoginCommand;
import ken.vivid.application.port.input.auth.saveUser.LoginUseCase;
import ken.vivid.application.port.input.auth.LogoutUseCase;
import ken.vivid.application.port.input.auth.saveUser.RegisterUseCase;
import ken.vivid.application.port.input.auth.saveUser.StoreCommand;
import ken.vivid.adapter.exception.auth.InvalidCredentialsException;
import ken.vivid.adapter.exception.auth.UserAlreadyExistsException;
import ken.vivid.application.port.output.auth.*;
import ken.vivid.domain.entities.AuthResult;
import ken.vivid.domain.entities.User;
import ken.vivid.domain.dto.Role;

public class AuthService implements LoginUseCase,
        LogoutUseCase, RegisterUseCase, GetCurrentUserUseCase {

    private final LoadUser loadUser;
    private final SaveUser saveUser;
    private final PasswordEncoder passwordEncoder;
    private final TokenGenerator tokenGenerator;
    private final TokenBlacklist tokenBlacklist;

    public AuthService(LoadUser loadUser, SaveUser saveUser, PasswordEncoder passwordEncoder, TokenGenerator tokenGenerator, TokenBlacklist tokenBlacklist) {
        this.loadUser = loadUser;
        this.saveUser = saveUser;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
        this.tokenBlacklist = tokenBlacklist;
    }

    @Override
    public User getCurrentUser(String email) {
        return loadUser.loadByEmailOrUserName(email).orElseThrow(() -> new InvalidCredentialsException("User not found"));
    }

    @Override
    public AuthResult login(LoginCommand loginCommand) {

        //TODO Define Validations proper to the application layer
        User user = loadUser.loadByEmailOrUserName(loginCommand.email())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if(!passwordEncoder.matches(loginCommand.rawPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if(!user.getActive()) {
            throw new InvalidCredentialsException("User is not active");
        }

        String token = tokenGenerator.generateToken(user);
        return new AuthResult(token, tokenGenerator.getExpirationMillis(), user);
    }

    @Override
    public void logout(String token) {
        tokenBlacklist.revoke(token);
    }

    @Override
    public AuthResult register(StoreCommand storeCommand) {

        if (loadUser.existsByEmail(storeCommand.email())) {
            throw new UserAlreadyExistsException("An account already exists with this email : " + storeCommand.email());
        }

        User newUser = User.createUser(
                storeCommand.id(),
                Role.CUSTOMER,
                storeCommand.firstName(),
                storeCommand.lastName(),
                storeCommand.userName(),
                storeCommand.phone(),
                storeCommand.email(),
                passwordEncoder.hash(storeCommand.rawPassword())
        );

        User savedUser = saveUser.save(newUser);
        String token = tokenGenerator.generateToken(savedUser);
        return new AuthResult(token, tokenGenerator.getExpirationMillis(), savedUser);
    }

    @Override
    public User store(StoreCommand storeCommand) {

        if (loadUser.existsByEmail(storeCommand.email())) {
            throw new UserAlreadyExistsException("An account already exits with this emain : " + storeCommand.email());
        }

        User newUser = User.createUser(
                storeCommand.id(),
                storeCommand.role(),
                storeCommand.firstName(),
                storeCommand.lastName(),
                storeCommand.userName(),
                storeCommand.phone(),
                storeCommand.email(),
                passwordEncoder.hash(storeCommand.rawPassword())
        );

        return saveUser.save(newUser);
    }
}
