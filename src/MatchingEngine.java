public class MatchingEngine {

    public static boolean isMatch(
            Request request,
            Resource resource) {

        boolean categoryMatch =
                request.getCategory()
                        .equalsIgnoreCase(
                                resource.getCategory()
                        );

        boolean quantityMatch =
                resource.getQuantity()
                        >= request.getQuantity();

        boolean available =
                resource.getAvailability()
                        .equalsIgnoreCase("Available");

        return categoryMatch &&
               quantityMatch &&
               available;
    }


    public static int calculateScore(
            Request request,
            Resource resource,
            double distanceKm) {

        int score = 0;

        // Category match = 40
        if (request.getCategory()
                .equalsIgnoreCase(
                        resource.getCategory())) {

            score += 40;
        }


        // Same text location = 20
        if (request.getLocation()
                .equalsIgnoreCase(
                        resource.getLocation())) {

            score += 20;
        }


        // Quantity available = 20
        if (resource.getQuantity()
                >= request.getQuantity()) {

            score += 20;
        }


        // Description/context = 10
        if (hasDescriptionMatch(
                request.getDescription(),
                resource.getDescription())) {

            score += 10;
        }


        // Distance = 10
        if (distanceKm >= 0) {

            if (distanceKm <= 2) {
                score += 10;
            }

            else if (distanceKm <= 5) {
                score += 8;
            }

            else if (distanceKm <= 10) {
                score += 6;
            }

            else if (distanceKm <= 25) {
                score += 4;
            }

            else {
                score += 2;
            }
        }


        return score;
    }


    private static boolean hasDescriptionMatch(
            String requestDescription,
            String resourceDescription) {

        if (requestDescription == null ||
                resourceDescription == null) {

            return false;
        }

        String[] requestWords =
                requestDescription
                        .toLowerCase()
                        .split("\\s+");

        String resourceText =
                resourceDescription.toLowerCase();


        for (String word : requestWords) {

            word =
                    word.replaceAll(
                            "[^a-zA-Z0-9]",
                            ""
                    );


            if (word.length() < 3) {
                continue;
            }


            if (resourceText.contains(word)) {
                return true;
            }
        }


        return false;
    }


    public static void showMatch(
            Request request,
            Resource resource) {

        if (isMatch(
                request,
                resource)) {

            int score =
                    calculateScore(
                            request,
                            resource,
                            -1
                    );

            System.out.println(
                    "\nMATCH FOUND!"
            );

            System.out.println(
                    "Request Category : "
                            + request.getCategory()
            );

            System.out.println(
                    "Resource : "
                            + resource.getDescription()
            );

            System.out.println(
                    "Location : "
                            + resource.getLocation()
            );

            System.out.println(
                    "Priority : "
                            + request.getPriority()
            );

            System.out.println(
                    "Match Score : "
                            + score + "%"
            );

        }

        else {

            System.out.println(
                    "\nNo suitable resource found."
            );
        }
    }
}