package com.gof.ICNBack.Service;

import com.gof.ICNBack.DataSources.EmailDao;
import com.gof.ICNBack.Utils.Config;
import com.gof.ICNBack.Utils.CodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class EmailService {
    @Autowired
    EmailDao emailDao;

    public String generateValidationCode(String email) {
        int time = 0;
        while (time < Config.getInt("service.email.maxGeneratingTime")) {
            String code = CodeGenerator.generateCode(Config.getInt("service.email.codeLength"));
            boolean result = emailDao.createRecipe(email, code);
            // @TODO: implements email sender
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
