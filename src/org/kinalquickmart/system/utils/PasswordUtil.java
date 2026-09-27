package org.kinalquickmart.system.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;


public final class PasswordUtil {

    private static final int COSTO = 12;

    private PasswordUtil() {
    }

    public static String hash(String passwordPlano) {
        return BCrypt.withDefaults().hashToString(COSTO, passwordPlano.toCharArray());
    }


    public static boolean verificar(String passwordPlano, String hashGuardado) {
        if (passwordPlano == null || passwordPlano.isEmpty()
                || hashGuardado == null || hashGuardado.isBlank()) {
            return false;
        }
        if (!esHashBCrypt(hashGuardado)) {
            return false;
        }
        BCrypt.Result resultado = BCrypt.verifyer().verify(passwordPlano.toCharArray(), hashGuardado.toCharArray());
        return resultado.verified;
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