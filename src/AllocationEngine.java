public class AllocationEngine {

    public static int allocateResource(
            Request request,
            Resource resource) {

        if (!MatchingEngine.isMatch(
                request,
                resource)) {

            System.out.println(
                    "Allocation failed: No valid match."
            );

            return 0;
        }

        int allocatedQuantity =
                request.getQuantity();

        System.out.println(
                "\n--- RESOURCE ALLOCATION ---"
        );

        System.out.println(
                "Request : "
                        + request.getRequesterName()
        );

        System.out.println(
                "Resource : "
                        + resource.getDescription()
        );

        System.out.println(
                "Quantity : "
                        + allocatedQuantity
        );

        System.out.println(
                "Location : "
                        + resource.getLocation()
        );

        if (resource.getQuantity()
                == allocatedQuantity) {

            resource.setAvailability(
                    "Allocated"
            );
        }

        request.setStatus(
                "Allocated"
        );

        System.out.println(
                "Allocation Status : Successful"
        );

        return allocatedQuantity;
    }
}