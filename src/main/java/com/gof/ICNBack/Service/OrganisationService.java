package com.gof.ICNBack.Service;

import com.gof.ICNBack.DataSources.Organisation.OrganisationDao;
import com.gof.ICNBack.DataSources.User.UserDao;
import com.gof.ICNBack.Entity.Organisation;
import com.gof.ICNBack.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class OrganisationService {

    @Autowired
    OrganisationDao organisationDao;

    @Autowired
    UserDao userDao;

    /**
     * locations: map scale for searching
     * @param filterParameters: optional parameters for further restriction.
     * @param searchString: optional keywords
     * @param limit: result length
     * @param skip: default 0, number of result skipped
     * @return :searching result from Dao layer, list of organisation Cards
     * */
    public List<Organisation.OrganisationCard> getOrgCards(int locationX, int locationY, int lenX, int lenY, Map<String, String> filterParameters, String searchString, Integer skip, Integer limit) {
        return organisationDao.searchOrganisationCards(locationX,locationY,lenX,lenY, filterParameters, searchString, skip, limit);

    }

    /**
     * @param organisationId: specific id for unique organisation
     * @param user: used to restrict searching result.
     * @return :searching result from Dao layer, a full or residual organisation object
     * &#064;TODO:  complete the switch section basing on feature list.  */
    public Organisation getOrg(String organisationId, String user){
        User user1 = userDao.getUserById(user);
        Organisation org = organisationDao.getOrganisationById(organisationId);
        switch (user1.getVIP()){
            case -1:
            case 0:
                return null; // free users/visitors are unable to access detailed information
            case 1:
                break;
            case 2:
                break;
            default:
        }
        return org;
    }

    public List<Organisation.OrganisationCard> getOrgCardsByIds(List<String> ids) {
        return organisationDao.getOrgCardsByIds(ids);
    }
}
