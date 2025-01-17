package sn.sonaged.login1.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import sn.sonaged.login1.exception.CustomAuthenticationException;
import sn.sonaged.login1.repository.UserRepository;
import sn.sonaged.login1.entities.User;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service
public class PasswordResetService
{

    private JavaMailSender javaMailSender;
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;

    public PasswordResetService(JavaMailSender javaMailSender, PasswordEncoder passwordEncoder, UserRepository userRepository)
    {
        this.javaMailSender = javaMailSender;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Value("${app.secret-key}")
    private String SECRET_KEY;

    @Value("${app.expiration-time}")
    private long jwtExpiration;

    public void initiatePasswordReset(String matricule) throws MessagingException
    {
        User user = userRepository.findByMatricule(matricule)
                .orElseThrow(() -> new CustomAuthenticationException("User not found with matricule: " + matricule));

        String token = generatePasswordResetToken(user);
        sendPasswordResetEmail(user.getEmail(), token);
    }

    public void resetPassword(String token, String newPassword)
    {
        if (isTokenValid(token))
        {
            String matricule = extractMatricule(token);
            User user = userRepository.findByMatricule(matricule)
                    .orElseThrow(() -> new CustomAuthenticationException("User not found with matricule: " + matricule));

            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } else
        {
            throw new CustomAuthenticationException("Invalid or expired token");
        }
    }

    private String generatePasswordResetToken(User user)
    {
        Map<String, Object> claims = new HashMap<>();
        claims.put("matricule", user.getMatricule());
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    private void sendPasswordResetEmail(String toEmail, String token) throws MessagingException
    {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        try
        {
            helper.setTo(toEmail);
            helper.setSubject("Password Reset Request");
            helper.setText("To reset your password, click the link below:\n" +
                    "http://localhost:9899/auth/reset-password?token=" + token, true);
            javaMailSender.send(message);
        } catch (MessagingException e)
        {
            throw new CustomAuthenticationException("Failed to send password reset email", e);
        }
    }

    private boolean isTokenValid(String token)
    {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private String extractMatricule(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .get("matricule", String.class);
    }


    @ExceptionHandler(CustomAuthenticationException.class)
    public ResponseEntity<String> handleCustomAuthenticationException(CustomAuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }
}
