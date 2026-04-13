package uspace.api.cruise.emergencyShuttle;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import uspace.application.cruise.emergencyShuttle.EmergencyShuttleService;
import uspace.application.cruise.emergencyShuttle.dtos.EmergencyShuttleManifestDto;

@Path("/cruises/{cruiseId}/emergencyShuttles")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EmergencyShuttleResource {

    private final EmergencyShuttleService emergencyShuttleService;

    @Inject
    public EmergencyShuttleResource(EmergencyShuttleService emergencyShuttleService) {
        this.emergencyShuttleService = emergencyShuttleService;
    }

    @GET
    public Response getManifest(@PathParam("cruiseId") String cruiseId) {
        EmergencyShuttleManifestDto manifest = emergencyShuttleService.getManifest(cruiseId);
        return Response.ok(manifest).build();
    }
}
