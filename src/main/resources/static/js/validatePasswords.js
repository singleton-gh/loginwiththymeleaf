        function validatePasswords(event)
        {
        // Récupérer les valeurs des champs de mot de passe
        const newPassword = document.getElementById("newPassword").value;
        const confirmPassword = document.getElementById("confirmPassword").value;

        // Vérification de la longueur minimale
        if (newPassword.length < 8)
        {
        alert("Le mot de passe doit contenir au moins 8 caractères.");
        event.preventDefault();
        return false;
       }

        // Comparer les mots de passe
        if (newPassword !== confirmPassword)
        {
        alert("Les mots de passe ne correspondent pas.");
        event.preventDefault();
        return false;
       }

        return true;
    }


        document.getElementById('reset-password-form').addEventListener('submit', function(event) {
            const email = document.getElementById('email').value;
            const emailError = document.getElementById('email-error');

            if (!isValidEmail(email)) {
                emailError.textContent = "L'adresse email fournie est invalide.";
                event.preventDefault(); // Empêche la soumission du formulaire
            } else {
                emailError.textContent = ""; // Efface le message d'erreur
            }
        });

        function isValidEmail(email) {
            const regex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
            return regex.test(email);
        }
