package com.gof.ICNBack.DataSources.Email;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Transactional
public abstract class EmailDao {
    public abstract void createRecipe(String email, String code);

    public abstract List<String> getCodeByEmail(String email, LocalDateTime latestTime);
}
