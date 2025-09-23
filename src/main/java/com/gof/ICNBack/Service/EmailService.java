package com.gof.ICNBack.Service;

import com.gof.ICNBack.DataSources.EmailDao;
import com.gof.ICNBack.Utils.CodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Component
@Transactional
public class EmailService {
    @Autowired
    EmailDao emailDao;
    @Autowired
    Environment env;

    public String generateValidationCode(String email) {
        int time = 0;
        while (time <
                Integer.parseInt(
                        Objects.requireNonNull(
                                env.getProperty(
                                        "app.service.email.maxGeneratingTime")))) {
            String code =
                    CodeGenerator.generateCode(
                            Integer.parseInt(
                                    Objects.requireNonNull(
                                            env.getProperty(
                                                    "app.service.email.codeLength"))));
            boolean result = emailDao.createRecipe(email, code);
            if (result) {
                return code;
            }
            time++;
        }
        return null;
    }

    public String getValidationCode(String email) {
        return emailDao.getCodeByEmail(email);
    }
}
