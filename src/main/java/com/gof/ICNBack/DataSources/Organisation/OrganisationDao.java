package com.gof.ICNBack.DataSources.Organisation;

import com.gof.ICNBack.Entity.Organisation;

import java.util.List;
import java.util.Map;

public abstract class OrganisationDao {
    public abstract List<Organisation> searchOrganisations(double locationX, double locationY, double endX, double endY, Map<String, String> filterParameters, String searchString, Integer skip, Integer limit);

    public abstract Organisation getOrganisationById(String organisationId);

    public abstract List<Organisation.OrganisationCard> getOrgCardsByIds(List<String> orgIds);

    public abstract List<Organisation> getOrganisationsWithoutGeocode();

    public abstract void updateGeocode(List<Organisation> orgs);
}
