package com.gof.ICNBack.Web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gof.ICNBack.Entity.Organisation;
import com.gof.ICNBack.Service.OrganisationService;
import com.gof.ICNBack.Web.Entity.SearchOrganisationRequest;
import com.gof.ICNBack.Web.Utils.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.gof.ICNBack.Web.Utils.Validator.isValidUserId;


@RestController
@RequestMapping("/organisation")
public class OrganisationController {

    @Autowired
    OrganisationService orgRepo;

    @GetMapping("/general")
    public ResponseEntity<List<Organisation.OrganisationCard>> searchOrganisation(
            SearchOrganisationRequest request
    ) {
        try {
            List<Organisation.OrganisationCard> result = orgRepo.getOrgCards(request);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("X-Error","Invalid input")
                    .build();
        }
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
        if (!isValidUserId(user)) return null;
        Organisation result = orgRepo.getOrg(organisationId, user);
        return result == null ?
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .header("X-Error", "unable to get company details")
                        .build():
                ResponseEntity.status(HttpStatus.OK)
                        .body(result);
    }
}
