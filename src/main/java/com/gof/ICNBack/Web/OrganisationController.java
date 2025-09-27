package com.gof.ICNBack.Web;

import com.gof.ICNBack.Entity.Organisation;
import com.gof.ICNBack.Service.OrganisationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/organisation")
public class OrganisationController {

    @Autowired
    OrganisationService orgRepo;

    @GetMapping("/general")
    public List<Organisation.OrganisationCard> searchOrganisation(
            @RequestParam(required = true) int locationX,
            @RequestParam(required = true) int locationY,
            @RequestParam(required = true) int lenX,
            @RequestParam(required = true) int lenY,
            @RequestParam(required = false) Map<String, String> filterParameters,
            @RequestParam(required = false) String searchString,
            @RequestParam(required = false) Integer skip,
            @RequestParam(required = false) Integer limit
    ) {
        return orgRepo.getOrgCards(locationX,locationY,lenX,lenY, filterParameters, searchString, skip, limit);
    }

    @GetMapping("/generalByIds")
    public List<Organisation.OrganisationCard> searchOrgByIds(
            @RequestParam(required = true) List<String> ids
    ) {
        return orgRepo.getOrgCardsByIds(ids);
    }

    @GetMapping("/specific")
    public ResponseEntity<Organisation> searchOrganisationDetail(
            @RequestParam(required = true) String organisationId,
            @RequestParam(required = true) String user
    ) {
        Organisation result = orgRepo.getOrg(organisationId, user);
        return result == null ?
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .header("unable to get company details")
                        .build():
                ResponseEntity.status(HttpStatus.ACCEPTED)
                        .body(result);
    }
}
