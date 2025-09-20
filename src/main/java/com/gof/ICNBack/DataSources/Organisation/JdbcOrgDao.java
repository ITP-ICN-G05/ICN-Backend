package com.gof.ICNBack.DataSources.Organisation;

import com.gof.ICNBack.Entity.Organisation;

import java.util.List;
import java.util.Map;

public class JdbcOrgDao extends OrganisationDao {


    @Override
    public List<Organisation> searchOrganisations(String location, Map<String, String> filterParameters, String searchString, Integer skip, Integer limit) {
        return null;
    }

    @Override
    public Organisation getOrganisationById(String organisationId) {
        return null;
    }

    @Override
    public List<Organisation.OrganisationCard> getOrgCardsByIds(List<String> orgIds) {
        return null;
    }
}
