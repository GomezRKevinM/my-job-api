package com.tecnil.my_job_api.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCrypt {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * Genera un hash BCrypt a partir de una contraseña en texto plano.
     *
     * @param rawPassword La contraseña que ingresó el usuario.
     * @return El hash generado para guardar en la base de datos.
     */
    public static String encode(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
        return encoder.encode(rawPassword);
    }

    /**
     * Verifica si una contraseña en texto plano coincide con el hash guardado.
     *
     * @param rawPassword La contraseña introducida en el login (texto plano).
     * @param encodedPassword El hash recuperado de la base de datos.
     * @return true si coinciden, false en caso contrario.
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return encoder.matches(rawPassword, encodedPassword);
    }
}
