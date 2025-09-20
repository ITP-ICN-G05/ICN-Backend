package com.gof.ICNBack.DataSources.Organisation;

import com.gof.ICNBack.Entity.Organisation;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public abstract class OrganisationDao {
    public abstract List<Organisation> searchOrganisations(String location, Map<String, String> filterParameters, String searchString, Integer skip, Integer limit);

    public abstract Organisation getOrganisationById(String organisationId);

    public abstract List<Organisation.OrganisationCard> getOrgCardsByIds(List<String> orgIds);
}
