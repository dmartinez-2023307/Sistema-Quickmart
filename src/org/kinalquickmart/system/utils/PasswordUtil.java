package org.kinalquickmart.system.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Encriptación (hash) de contraseñas usando BCrypt (librería at.favre.lib:bcrypt).
 * Requiere en el classpath: bcrypt-0.10.2.jar y bytes-1.6.1.jar.
 */
public final class PasswordUtil {

    private static final int COSTO = 12;

    private PasswordUtil() {
    }

    /** Genera el hash que se guarda en la columna password de la tabla Usuario. */
    public static String hash(String passwordPlano) {
        return BCrypt.withDefaults().hashToString(COSTO, passwordPlano.toCharArray());
    }

    /**
     * Compara la contraseña escrita por el usuario contra el valor guardado en la BD.
     * Si lo guardado es un hash BCrypt lo verifica con BCrypt; si es texto plano
     * (como el admin semilla del DML.sql, que no pasa por Java) lo compara directo.
     */
    public static boolean verificar(String passwordPlano, String hashGuardado) {
        if (passwordPlano == null || hashGuardado == null || hashGuardado.isBlank()) {
            return false;
        }
        if (esHashBCrypt(hashGuardado)) {
            BCrypt.Result resultado = BCrypt.verifyer().verify(passwordPlano.toCharArray(), hashGuardado.toCharArray());
            return resultado.verified;
        }
        return passwordPlano.equals(hashGuardado);
    }

    private static boolean esHashBCrypt(String valor) {
        return valor.startsWith("$2a$") || valor.startsWith("$2b$") || valor.startsWith("$2y$");
    }

    public static void main(String[] args) {
        String[] passwords = args.length > 0 ? args : new String[] { "12345", "4567867", "1238" };
        for (String clave : passwords) {
            System.out.println(clave + "  ->  " + hash(clave));
        }
    }
}