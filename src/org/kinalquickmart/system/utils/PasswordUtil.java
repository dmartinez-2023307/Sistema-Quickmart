package org.kinalquickmart.system.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Encriptación (hash) de contraseñas con PBKDF2-HMAC-SHA256 + salt aleatorio.
 * Solo usa clases del JDK: no hace falta agregar ningún .jar.
 *
 * Formato guardado en Usuario.password:  iteraciones:salt:hash  (salt y hash en Base64)
 */
public final class PasswordUtil {

    private static final int ITERACIONES = 65_536;
    private static final int LONGITUD_SALT = 16;   // bytes
    private static final int LONGITUD_HASH = 32;   // bytes (256 bits)

    private PasswordUtil() {
    }

    /** Genera el hash que se guarda en la base de datos. */
    public static String hash(String passwordPlano) {
        byte[] salt = new byte[LONGITUD_SALT];
        new SecureRandom().nextBytes(salt);
        byte[] hash = pbkdf2(passwordPlano.toCharArray(), salt, ITERACIONES, LONGITUD_HASH);
        return ITERACIONES + ":"
                + Base64.getEncoder().encodeToString(salt) + ":"
                + Base64.getEncoder().encodeToString(hash);
    }

    /** Compara una contraseña escrita por el usuario contra el hash guardado. */
    public static boolean verificar(String passwordPlano, String hashGuardado) {
        if (passwordPlano == null || hashGuardado == null) {
            return false;
        }
        try {
            String[] partes = hashGuardado.split(":");
            if (partes.length != 3) {
                return false; // no es un hash válido (p. ej. una contraseña vieja en texto plano)
            }
            int iteraciones = Integer.parseInt(partes[0]);
            byte[] salt = Base64.getDecoder().decode(partes[1]);
            byte[] esperado = Base64.getDecoder().decode(partes[2]);
            byte[] calculado = pbkdf2(passwordPlano.toCharArray(), salt, iteraciones, esperado.length);
            return MessageDigest.isEqual(esperado, calculado); // comparación en tiempo constante
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iteraciones, int bytes) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iteraciones, bytes * 8);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo calcular el hash de la contraseña", e);
        }
    }

    /** Utilidad para generar hashes desde consola:  java PasswordUtil.java miClave  */
    public static void main(String[] args) {
        for (String clave : args) {
            System.out.println(clave + "  ->  " + hash(clave));
        }
    }
}