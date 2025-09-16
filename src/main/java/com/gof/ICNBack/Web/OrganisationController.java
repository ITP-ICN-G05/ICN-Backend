package com.gof.ICNBack.Web;

import com.gof.ICNBack.Entity.Organisation;
import com.gof.ICNBack.Repositories.OrganisationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/organisation")
public class OrganisationController {

    @Autowired
    OrganisationRepository orgRepo;

    @GetMapping("/general")
    public List<Organisation.OrganisationCard> searchOrganisation(
            @RequestParam(required = true) String location,
            @RequestParam(required = true) List<String> filterParameters,
            @RequestParam(required = false) String searchString,
            @RequestParam(required = false) Integer skip,
            @RequestParam(required = false) Integer limit
    ) {
        return orgRepo.getOrganisationCards(location, filterParameters, searchString, skip, limit);
    }

    @GetMapping("/specific")
    public Organisation searchOrganisationDetail(
            @RequestParam(required = true) String organisationId,
            @RequestParam(required = true) String user
    ) {
        return orgRepo.getOrg(organisationId, user);
    }
}
