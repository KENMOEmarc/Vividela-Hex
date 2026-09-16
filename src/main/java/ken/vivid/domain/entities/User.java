package ken.vivid.domain.entities;

import ken.vivid.domain.dto.Role;

public class User {
    private final Long id;
    private Role role;
    private String firstName;
    private String lastName;
    private String userName;
    private String phone;
    private String email;
    private String password;
    private Integer loyaltyPoints;
    private Boolean isActive;

    private User(Long id, Role role, String firstName, String lastName, String userName,
                 String phone, String email, String password) {
        this.id = id;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.isActive = true;
    }

    public static User createUser(Long id, Role role, String firstName, String lastName,
                                  String userName, String phone, String email, String password) {

        if (firstName.isBlank() || lastName.isBlank() || userName.isBlank() ||
                phone.isBlank() || email.isBlank() || password.isBlank() || role.describeConstable().isEmpty()) {
            throw new IllegalArgumentException("First name, last name, username, email, password, and role cannot be blank");
        }

       return new User(id, role, firstName, lastName, userName, phone, email, password);
    }

    public Long getId() {
        return id;
    }

    public Role getRole() {
        return role;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
