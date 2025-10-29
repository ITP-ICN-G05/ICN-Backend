package com.gof.ICNBack.Utils;

import java.security.SecureRandom;
import java.util.Random;

public class CodeGenerator {
    public static String generateCode(int length){
        String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder code = new StringBuilder();
        Random random = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            code.append(characters.charAt(index));
        }

        return code.toString();
    }
}
