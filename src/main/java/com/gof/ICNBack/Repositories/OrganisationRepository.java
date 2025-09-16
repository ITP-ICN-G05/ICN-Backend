package com.gof.ICNBack.Repositories;

import com.gof.ICNBack.DataSources.OrganisationDao;
import com.gof.ICNBack.DataSources.UserDao;
import com.gof.ICNBack.Entity.Organisation;
import com.gof.ICNBack.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

public class OrganisationRepository {

    @Autowired
    OrganisationDao organisationDao;

    @Autowired
    UserDao userDao;

    /**
     * @param location: map scale for searching, needs to be transfer to address
     * @param filterParameters: optional parameters for further restriction.
     * @param searchString: optional keywords
     * @param limit: result length
     * @param skip: default 0, number of result skipped
     * @return :searching result from Dao layer, list of organisation Cards
     * */
    public List<Organisation.OrganisationCard> getOrganisationCards(String location, List<String> filterParameters, String searchString, Integer skip, Integer limit) {
        List<Organisation> result = organisationDao.searchOrganisations(location, filterParameters, searchString, skip, limit);
        ArrayList<Organisation.OrganisationCard> cards = new ArrayList<>();
        for (Organisation org : result){
            cards.add(org.toCard());
        }
        return cards;
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
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            default:
        }
        return org;
    }
}
