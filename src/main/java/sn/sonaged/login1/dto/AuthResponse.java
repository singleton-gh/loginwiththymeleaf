package sn.sonaged.login1.dto;




import sn.sonaged.login1.entities.Role;


public class AuthResponse
{
    private String token;
    private Role role;
    private String message;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
