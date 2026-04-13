package uspace.api.cruise.cabin;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import uspace.application.cruise.cabin.CabinAttributionService;
import uspace.application.cruise.cabin.dtos.CabinAttributionResponseDto;
import uspace.domain.cruise.CruiseId;

@Path("/cruises/{cruiseId}/cabins")
@Produces(MediaType.APPLICATION_JSON)
public class CabinAttributionResource {

    private final CabinAttributionService service;

    @Inject
    public CabinAttributionResource(CabinAttributionService service) {
        this.service = service;
    }

    @GET
    public Response getCabinAttributions(
            @PathParam("cruiseId") String cruiseIdString,
            @QueryParam("criteria") String criteriaString
    ) {
        CabinAttributionResponseDto response =
                service.getAttributions(cruiseIdString, criteriaString);

        if (response.cabins.isEmpty()) {
            return Response.ok().build();
        }

        return Response.ok(response).build();
    }
}
