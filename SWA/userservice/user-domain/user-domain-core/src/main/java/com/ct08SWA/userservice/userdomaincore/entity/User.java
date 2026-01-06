package com.ct08SWA.userservice.userdomaincore.entity;

import com.ct08SWA.userservice.userdomaincore.event.UserCreatedEvent;
import com.ct08SWA.userservice.userdomaincore.exception.UserDomainException;
import com.ct08SWA.userservice.userdomaincore.valueobject.UserId;
import com.ct08SWA.userservice.userdomaincore.valueobject.UserRole;

import java.util.UUID;

/**
 * Aggregate Root: User.
 * (Không Lombok, Pure Java)
 */
public class User extends AggregateRoot<UserId> {
    private UserId id;
    private final String username;
    private final String password; // Password đã mã hóa
    private final String email;
    private final String firstName;
    private final String lastName;
    private final UserRole role;
    private  boolean active;

    // Constructor (private, dùng Builder)
    private User(Builder builder) {
        this.id = builder.id;
        this.username = builder.username;
        this.password = builder.password;
        this.email = builder.email;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.role = builder.role;
        this.active = builder.active;
    }

    // --- Logic Nghiệp vụ ---

    /**
     * Khởi tạo User mới (khi đăng ký).
     */
    public void initializeUser() {
        this.id = new UserId(UUID.randomUUID());
        this.addDomainEvent(new UserCreatedEvent(this.id.getValue().toString()));
        // Các validation khác nếu cần
    }

    public void validate() {
        if (username == null || username.isEmpty()) {
            throw new UserDomainException("Username cannot be empty");
        }
        if (email == null || email.isEmpty()) {
           throw new UserDomainException("Email cannot be empty!");
        }
        // ... thêm các rule khác
    }
    public void unblock(){
        if(this.role==UserRole.ADMIN){
            throw new UserDomainException("Cannot active administrator");

        }
        else if(active){
            throw new UserDomainException("User is already unblock");
        }
        else{
            this.active=true;
        }
    }

    public void block(){
        if(this.role==UserRole.ADMIN){
            throw new UserDomainException("Cannot active administrator");

        }
        else if(!active){
            throw new UserDomainException("User is already block");
        }
        else{
            this.active=false;
        }
    }
    // --- Getters ---
    public UserId getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public UserRole getRole() { return role; }
    public boolean isActive() { return active; }


    // --- Builder ---
    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private UserId id;
        private String username;
        private String password;
        private String email;
        private String firstName;
        private String lastName;
        private UserRole role;
        private boolean active;

        private Builder() {}

        public Builder id(UserId val) { id = val; return this; }
        public Builder username(String val) { username = val; return this; }
        public Builder password(String val) { password = val; return this; }
        public Builder email(String val) { email = val; return this; }
        public Builder firstName(String val) { firstName = val; return this; }
        public Builder lastName(String val) { lastName = val; return this; }
        public Builder role(UserRole val) { role = val; return this; }
        public Builder active(boolean val) { active = val; return this; }

        public User build() {
            return new User(this);
        }
    }
}