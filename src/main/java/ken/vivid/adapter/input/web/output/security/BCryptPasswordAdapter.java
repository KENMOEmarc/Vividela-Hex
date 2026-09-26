package ken.vivid.adapter.input.web.output.security;

import ken.vivid.application.port.output.auth.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BCryptPasswordAdapter implements PasswordEncoder {

    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    public String hash(String rawPassword) {
        String hashed = passwordEncoder.encode(rawPassword);
        log.debug("Password hashed successfully");
        return hashed;
    }

    @Override
    public boolean matches(String rawPassword, String hashedPassword) {
        boolean matches = passwordEncoder.matches(rawPassword, hashedPassword);
        log.debug("Password comparison result={}", matches);
        return matches;
    }
}
