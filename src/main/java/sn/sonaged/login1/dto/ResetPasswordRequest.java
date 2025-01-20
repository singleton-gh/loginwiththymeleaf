package sn.sonaged.login1.dto; // Assurez-vous que le package correspond à votre structure de projet



public class ResetPasswordRequest
{
    private String email;
    private String matricule;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }
}