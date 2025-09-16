package com.gof.ICNBack.DataSources;

import com.gof.ICNBack.Entity.Organisation;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional
public class OrganisationDao {
    public List<Organisation> searchOrganisations(String location, List<String> filterParameters, String searchString, Integer skip, Integer limit) {
         return null;
    }

    public Organisation getOrganisationById(String organisationId) {
        return null;
    }
}
