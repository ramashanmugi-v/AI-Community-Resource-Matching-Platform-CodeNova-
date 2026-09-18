public class PriorityEngine {

    public static String calculatePriority(
            Request request) {

        String category =
                request.getCategory().toLowerCase();

        String description =
                request.getDescription().toLowerCase();

        if (category.contains("medical") ||
            category.contains("emergency") ||
            description.contains("urgent") ||
            description.contains("emergency")) {

            return "High";
        }

        if (category.contains("food") ||
            category.contains("water") ||
            category.contains("shelter")) {

            return "Medium";
        }

        return "Low";
    }
}