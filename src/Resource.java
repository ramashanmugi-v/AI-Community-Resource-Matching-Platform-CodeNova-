public class Resource {

    private int resourceId;
    private String providerName;
    private String category;
    private String description;
    private int quantity;
    private String location;
    private String availability;

    public Resource(
            String providerName,
            String category,
            String description,
            int quantity,
            String location) {

        this.providerName = providerName;
        this.category = category;
        this.description = description;
        this.quantity = quantity;
        this.location = location;
        this.availability = "Available";
    }

    public int getResourceId() {
        return resourceId;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getLocation() {
        return location;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public void displayResource() {

        System.out.println("\n--- Resource Details ---");

        System.out.println(
                "Provider     : " + providerName
        );

        System.out.println(
                "Category     : " + category
        );

        System.out.println(
                "Description  : " + description
        );

        System.out.println(
                "Quantity     : " + quantity
        );

        System.out.println(
                "Location     : " + location
        );

        System.out.println(
                "Availability : " + availability
        );
    }
}