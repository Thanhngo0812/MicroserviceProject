package com.ct08SWA.userservice.userapplicationservice.dto.inputdto;

/**
 * DTO chứa thông tin đăng ký (từ client gửi lên).
 */
public class RegisterUserCommand {
    private final String username;
    private final String password;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String role; // "USER" hoặc "ADMIN"

    public RegisterUserCommand(String username, String password, String email, String firstName, String lastName, String role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getRole() { return role; }
}