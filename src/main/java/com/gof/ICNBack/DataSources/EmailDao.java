package com.gof.ICNBack.DataSources;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

//@TODO: not implemented yet
@Component
@Transactional
public class EmailDao {
    public boolean createRecipe(String email, String code) {
        return false;
    }

    public String getCodeByEmail(String email) {
        return null;
    }
}
