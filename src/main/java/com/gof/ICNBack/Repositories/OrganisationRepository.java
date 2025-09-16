package com.gof.ICNBack.Repositories;

import com.gof.ICNBack.DataSources.OrganisationDao;
import com.gof.ICNBack.DataSources.UserDao;
import com.gof.ICNBack.Entity.Organisation;
import com.gof.ICNBack.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class OrganisationRepository {

    @Autowired
    OrganisationDao organisationDao;

    @Autowired
    UserDao userDao;

    public List<Organisation.OrganisationCard> getOrganisationCards(String location, List<String> filterParameters, String searchString, Integer skip, Integer limit) {
        return null;
    }

    public Organisation getOrg(String organisationId, String user){
        User user1 = userDao.getUserById(user);
        return null;
    }
}
