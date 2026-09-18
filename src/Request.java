public class Request {

    private int requestId;
    private String requesterName;
    private String category;
    private String description;
    private int quantity;
    private String location;
    private String priority;
    private String status;

    public Request(
            String requesterName,
            String category,
            String description,
            int quantity,
            String location) {

        this.requesterName = requesterName;
        this.category = category;
        this.description = description;
        this.quantity = quantity;
        this.location = location;
        this.priority = "Normal";
        this.status = "Pending";
    }

    public int getRequestId() {
        return requestId;
    }

    public String getRequesterName() {
        return requesterName;
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

    public String getPriority() {
        return priority;
    }

    public String getStatus() {
        return status;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void displayRequest() {

        System.out.println("\n--- Request Details ---");

        System.out.println(
                "Requester : " + requesterName
        );

        System.out.println(
                "Category  : " + category
        );

        System.out.println(
                "Description : " + description
        );

        System.out.println(
                "Quantity  : " + quantity
        );

        System.out.println(
                "Location  : " + location
        );

        System.out.println(
                "Priority  : " + priority
        );

        System.out.println(
                "Status    : " + status
        );
    }
}