package ken.vivid.support;

import ken.vivid.domain.dto.Role;
import ken.vivid.domain.entities.User;

/**
 * Test builder for {@link User}.
 * <p>
 * {@code User.createUser} does not validate the identifier: a null id is
 * therefore allowed here (case of a creation before persistence).
 */
public final class UserTestBuilder {

    private Long id = 1L;
    private Role role = Role.CUSTOMER;
    private String firstName = "Marc";
    private String lastName = "KENMOE";
    private String userName = "ken47";
    private String phone = "690000000";
    private String email = "marc@vividela.cm";
    private String password = "$2a$10$testHash";
    private boolean active = true;

    private UserTestBuilder() {
    }

    public static UserTestBuilder aUser() {
        return new UserTestBuilder();
    }

    public UserTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public UserTestBuilder withRole(Role role) {
        this.role = role;
        return this;
    }

    public UserTestBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public UserTestBuilder withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public UserTestBuilder withUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public UserTestBuilder withPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public UserTestBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public UserTestBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public UserTestBuilder inactive() {
        this.active = false;
        return this;
    }

    public User build() {
        User user = User.createUser(id, role, firstName, lastName, userName, phone, email, password);
        user.setActive(active);
        return user;
    }
}