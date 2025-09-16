package com.gof.ICNBack.Service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class EmailService {
    public String getValidationCode(String email) {
        return null;
    }
}
