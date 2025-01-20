package sn.sonaged.login1.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sn.sonaged.login1.exception.CustomAuthenticationException;
import sn.sonaged.login1.exception.EmailSendingException;
import sn.sonaged.login1.exception.UserNotFoundException;
import sn.sonaged.login1.repository.UserRepository;
import sn.sonaged.login1.entities.User;

import javax.crypto.SecretKey;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class PasswordResetService {

    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JavaMailSenderImpl mailSender;

    Logger log = LoggerFactory.getLogger(PasswordResetService.class);

    @Value("${app.secret-key}")
    private String SECRET_KEY;

    @Value("${app.expiration-time}")
    private long jwtExpiration;

    public PasswordResetService(JavaMailSender javaMailSender, PasswordEncoder passwordEncoder, UserRepository userRepository, JavaMailSenderImpl mailSender) {
        this.javaMailSender = javaMailSender;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    public boolean verifyUser(String email, String matricule) {
        User user = userRepository.findByEmailAndMatricule(email, matricule);
        return user != null;
    }

    public void sendResetLink(String email) {
        if (email == null || email.trim().isEmpty() || !isValidEmail(email)) {
            log.warn("Tentative avec une adresse e-mail invalide : {}", email);
            throw new IllegalArgumentException("Adresse e-mail invalide : " + (email == null ? "null" : email.trim()));
        }

        log.debug("Tentative d'envoi de lien de réinitialisation à l'adresse : {}", email);

        // Récupérer l'utilisateur associé à l'e-mail
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Utilisateur non trouvé pour l'adresse e-mail : {}", email);
                    return new UserNotFoundException("Utilisateur non trouvé avec l'e-mail : " + email);
                });

        try {
            // Générer le token de réinitialisation
            String token = generatePasswordResetToken(user);

            // Envoyer l'e-mail de réinitialisation
            sendPasswordResetEmail(email, token);

            log.info("Lien de réinitialisation envoyé avec succès à l'adresse : {}", email);
        } catch (MessagingException e) {
            log.error("Échec de l'envoi de l'email à l'adresse : {}", email, e);
            throw new EmailSendingException("Impossible d'envoyer l'e-mail de réinitialisation", e);
        } catch (Exception e) {
            log.error("Erreur inattendue lors de l'envoi du lien de réinitialisation pour l'adresse : {}", email, e);
            throw new RuntimeException("Erreur interne, veuillez réessayer plus tard", e);
        }
    }

    public boolean isValidEmail(String email) {
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void sendPasswordResetEmail(String toEmail, String token) throws MessagingException, UnsupportedEncodingException {
        if (toEmail == null || toEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("L'adresse email ne peut pas être vide.");
        }

        log.debug("Tentative d'envoi d'email à l'adresse : " + toEmail);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("noreply@sonaged.sn", "SONAGED Support");
        helper.setTo(toEmail);
        helper.setSubject("Réinitialisation de votre mot de passe");
        helper.setText("Pour réinitialiser votre mot de passe, cliquez sur le lien suivant : "
                + "http://localhost:9899/auth/reset-password?token=" + token, true);

        javaMailSender.send(message);
        log.debug("Email envoyé avec succès à l'adresse : " + toEmail);
    }

    public Boolean updatePassword(String token, String newPassword) {
        log.debug("Début de la réinitialisation du mot de passe avec le token : {}", token);

        // Validation du nouveau mot de passe
        if (newPassword == null || newPassword.length() < 8) {
            log.error("Le mot de passe doit contenir au moins 8 caractères.");
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 8 caractères.");
        }

        // Validation du token
        if (!isTokenValid(token)) {
            log.error("Token invalide ou expiré : {}", token);
            throw new CustomAuthenticationException("Invalid or expired token");
        }

        // Extraction du matricule du token
        String matricule = extractMatricule(token);
        log.debug("Matricule extrait du token : {}", matricule);

        // Recherche de l'utilisateur dans la base de données
        User user = userRepository.findByMatricule(matricule)
                .orElseThrow(() -> {
                    log.error("Utilisateur non trouvé avec le matricule : {}", matricule);
                    return new CustomAuthenticationException("User not found with matricule: " + matricule);
                });
        log.debug("Utilisateur trouvé : {}", user.getEmail());

        // Hachage du nouveau mot de passe
        String hashedPassword = passwordEncoder.encode(newPassword);
        log.debug("Nouveau mot de passe haché : {}", hashedPassword);

        // Mise à jour du mot de passe de l'utilisateur
        user.setPassword(hashedPassword);
        user.setResetToken(null); // Supprimer le token après utilisation

        try {
            userRepository.save(user);
            log.info("Mot de passe mis à jour pour l'utilisateur : {}", user.getEmail());
            return true; // Retourne true si la mise à jour réussit
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour de l'utilisateur : {}", e.getMessage());
            throw new CustomAuthenticationException("Erreur lors de la mise à jour du mot de passe");
        }
    }

    private String generatePasswordResetToken(User user) {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        Map<String, Object> claims = new HashMap<>();
        claims.put("matricule", user.getMatricule());

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        // Stocker le token dans la base de données
        user.setResetToken(token);
        userRepository.save(user);

        return token;
    }

    public boolean isTokenValid(String token) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
            SecretKey key = Keys.hmacShaKeyFor(keyBytes);

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public String extractMatricule(String token)
    {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("matricule", String.class);
    }

    public void sendPasswordUpdateConfirmation(String email)
    {
        String subject = "Confirmation de mise à jour du mot de passe";
        String message = "Bonjour,\n\nVotre mot de passe a été mis à jour avec succès. Si vous n'êtes pas à l'origine de cette action, veuillez contacter notre support immédiatement.\n\nCordialement,\nL'équipe.";
        javaMailSender(email, subject, message);
    }

    private void javaMailSender(String email, String subject, String message)
    {
        try
        {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom("noreply@sonaged.sn", "SONAGED Support");
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(message);

            javaMailSender.send(mimeMessage);
            log.info("Email de confirmation envoyé à : {}", email);
        } catch (MessagingException | UnsupportedEncodingException e)
        {
            log.error("Erreur lors de l'envoi de l'email de confirmation à : {}", email, e);
            throw new EmailSendingException("Erreur lors de l'envoi de l'email de confirmation", e);
        }
    }

}