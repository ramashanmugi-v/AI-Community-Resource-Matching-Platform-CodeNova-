import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        try {

            HttpServer server =
                    HttpServer.create(
                            new InetSocketAddress(8083),
                            0
                    );

            // ================= API ROUTES =================

            server.createContext(
                    "/api/request",
                    Main::handleRequest
            );

            server.createContext(
                    "/api/resource",
                    Main::handleResource
            );

            server.createContext(
                    "/api/matches",
                    Main::handleMatches
            );

            server.createContext(
                    "/api/allocate",
                    Main::handleAllocate
            );

            server.createContext(
                    "/api/dashboard",
                    Main::handleDashboard
            );

            server.setExecutor(null);

            System.out.println("====================================");
            System.out.println(" AI COMMUNITY RESOURCE MATCHING");
            System.out.println("====================================");
            System.out.println("Server started successfully!");
            System.out.println("Server URL: http://localhost:8083");

            server.start();

            // Keep backend running
            Thread.currentThread().join();

        } catch (Exception e) {

            System.out.println("Server failed to start!");
            e.printStackTrace();
        }
    }

    // =====================================================
    // REQUEST API
    // =====================================================

    private static void handleRequest(
            HttpExchange exchange) throws IOException {

        addHeaders(exchange);

        if (exchange.getRequestMethod()
                .equalsIgnoreCase("OPTIONS")) {

            exchange.sendResponseHeaders(200, -1);
            return;
        }

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            sendResponse(
                    exchange,
                    "Only POST method is allowed.",
                    405
            );
            return;
        }

        try {

            String body =
                    readBody(exchange);

            Map<String, String> data =
                    parseJson(body);

            String name =
                    data.get("name");

            String category =
                    data.get("category");

            String description =
                    data.get("description");

            String quantityText =
                    data.get("quantity");

            String location =
                    data.get("location");

            String latitudeText =
                    data.get("latitude");

            String longitudeText =
                    data.get("longitude");


            if (name == null ||
                    category == null ||
                    description == null ||
                    quantityText == null ||
                    location == null) {

                sendResponse(
                        exchange,
                        "Please fill all fields.",
                        400
                );

                return;
            }


            int quantity;

            try {

                quantity =
                        Integer.parseInt(
                                quantityText
                        );

            } catch (NumberFormatException e) {

                sendResponse(
                        exchange,
                        "Quantity must be a number.",
                        400
                );

                return;
            }


            Double latitude =
                    parseOptionalDouble(
                            latitudeText
                    );

            Double longitude =
                    parseOptionalDouble(
                            longitudeText
                    );


            Request request =
                    new Request(
                            name,
                            category,
                            description,
                            quantity,
                            location
                    );


            String priority =
                    PriorityEngine.calculatePriority(
                            request
                    );


            request.setPriority(priority);


            String sql =
                    "INSERT INTO requests " +
                    "(requester_name, category, description, " +
                    "quantity, location, priority, status, " +
                    "latitude, longitude) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";


            Connection con =
                    DBConnection.getConnection();


            if (con == null) {

                sendResponse(
                        exchange,
                        "Database connection failed.",
                        500
                );

                return;
            }


            try (
                    PreparedStatement ps =
                            con.prepareStatement(sql)
            ) {

                ps.setString(
                        1,
                        name
                );

                ps.setString(
                        2,
                        category
                );

                ps.setString(
                        3,
                        description
                );

                ps.setInt(
                        4,
                        quantity
                );

                ps.setString(
                        5,
                        location
                );

                ps.setString(
                        6,
                        priority
                );

                ps.setString(
                        7,
                        "Pending"
                );


                if (latitude != null) {

                    ps.setDouble(
                            8,
                            latitude
                    );

                } else {

                    ps.setNull(
                            8,
                            java.sql.Types.DECIMAL
                    );
                }


                if (longitude != null) {

                    ps.setDouble(
                            9,
                            longitude
                    );

                } else {

                    ps.setNull(
                            9,
                            java.sql.Types.DECIMAL
                    );
                }


                int result =
                        ps.executeUpdate();


                if (result > 0) {

                    System.out.println(
                            "Request saved successfully!"
                    );

                    System.out.println(
                            "Priority : "
                                    + priority
                    );


                    if (latitude != null &&
                            longitude != null) {

                        System.out.println(
                                "Live Location : "
                                        + latitude
                                        + ", "
                                        + longitude
                        );
                    }


                    sendResponse(
                            exchange,
                            "Request submitted successfully! Priority: "
                                    + priority,
                            200
                    );

                } else {

                    sendResponse(
                            exchange,
                            "Failed to save request.",
                            500
                    );
                }

            } finally {

                con.close();
            }

        } catch (Exception e) {

            e.printStackTrace();

            sendResponse(
                    exchange,
                    "Server error: "
                            + e.getMessage(),
                    500
            );
        }
    }

    // =====================================================
    // RESOURCE API
    // =====================================================

    private static void handleResource(
            HttpExchange exchange) throws IOException {

        addHeaders(exchange);

        if (exchange.getRequestMethod()
                .equalsIgnoreCase("OPTIONS")) {

            exchange.sendResponseHeaders(
                    200,
                    -1
            );

            return;
        }


        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            sendResponse(
                    exchange,
                    "Only POST method is allowed.",
                    405
            );

            return;
        }


        try {

            String body =
                    readBody(exchange);


            Map<String, String> data =
                    parseJson(body);


            String providerName =
                    data.get("providerName");

            String category =
                    data.get("category");

            String description =
                    data.get("description");

            String quantityText =
                    data.get("quantity");

            String location =
                    data.get("location");

            String latitudeText =
                    data.get("latitude");

            String longitudeText =
                    data.get("longitude");


            if (providerName == null ||
                    category == null ||
                    description == null ||
                    quantityText == null ||
                    location == null) {

                sendResponse(
                        exchange,
                        "Please fill all fields.",
                        400
                );

                return;
            }


            int quantity;


            try {

                quantity =
                        Integer.parseInt(
                                quantityText
                        );

            } catch (NumberFormatException e) {

                sendResponse(
                        exchange,
                        "Quantity must be a number.",
                        400
                );

                return;
            }


            Double latitude =
                    parseOptionalDouble(
                            latitudeText
                    );

            Double longitude =
                    parseOptionalDouble(
                            longitudeText
                    );


            String sql =
                    "INSERT INTO resources " +
                    "(provider_name, category, description, " +
                    "quantity, location, availability, " +
                    "latitude, longitude) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";


            Connection con =
                    DBConnection.getConnection();


            if (con == null) {

                sendResponse(
                        exchange,
                        "Database connection failed.",
                        500
                );

                return;
            }


            try (
                    PreparedStatement ps =
                            con.prepareStatement(sql)
            ) {

                ps.setString(
                        1,
                        providerName
                );

                ps.setString(
                        2,
                        category
                );

                ps.setString(
                        3,
                        description
                );

                ps.setInt(
                        4,
                        quantity
                );

                ps.setString(
                        5,
                        location
                );

                ps.setString(
                        6,
                        "Available"
                );


                if (latitude != null) {

                    ps.setDouble(
                            7,
                            latitude
                    );

                } else {

                    ps.setNull(
                            7,
                            java.sql.Types.DECIMAL
                    );
                }


                if (longitude != null) {

                    ps.setDouble(
                            8,
                            longitude
                    );

                } else {

                    ps.setNull(
                            8,
                            java.sql.Types.DECIMAL
                    );
                }


                int result =
                        ps.executeUpdate();


                if (result > 0) {

                    System.out.println(
                            "Resource saved successfully!"
                    );


                    if (latitude != null &&
                            longitude != null) {

                        System.out.println(
                                "Live Location : "
                                        + latitude
                                        + ", "
                                        + longitude
                        );
                    }


                    sendResponse(
                            exchange,
                            "Resource added successfully!",
                            200
                    );

                } else {

                    sendResponse(
                            exchange,
                            "Failed to save resource.",
                            500
                    );
                }

            } finally {

                con.close();
            }

        } catch (Exception e) {

            e.printStackTrace();

            sendResponse(
                    exchange,
                    "Server error: "
                            + e.getMessage(),
                    500
            );
        }
    }

    // =====================================================
    // MATCHING API
    // =====================================================

    private static void handleMatches(
            HttpExchange exchange) throws IOException {

        addHeaders(exchange);


        if (exchange.getRequestMethod()
                .equalsIgnoreCase("OPTIONS")) {

            exchange.sendResponseHeaders(
                    200,
                    -1
            );

            return;
        }


        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("GET")) {

            sendResponse(
                    exchange,
                    "Only GET method is allowed.",
                    405
            );

            return;
        }


        try {

            Connection con =
                    DBConnection.getConnection();


            if (con == null) {

                sendJson(
                        exchange,
                        "{\"error\":\"Database connection failed.\"}",
                        500
                );

                return;
            }


            String sql =
                    "SELECT " +
                    "r.request_id, " +
                    "r.requester_name, " +
                    "r.category AS request_category, " +
                    "r.description AS request_description, " +
                    "r.quantity AS request_quantity, " +
                    "r.location AS request_location, " +
                    "r.priority, " +
                    "r.latitude AS request_latitude, " +
                    "r.longitude AS request_longitude, " +

                    "res.resource_id, " +
                    "res.provider_name, " +
                    "res.category AS resource_category, " +
                    "res.description AS resource_description, " +
                    "res.quantity AS resource_quantity, " +
                    "res.location AS resource_location, " +
                    "res.latitude AS resource_latitude, " +
                    "res.longitude AS resource_longitude " +

                    "FROM requests r " +

                    "JOIN resources res " +

                    "ON LOWER(r.category) = " +
                    "LOWER(res.category) " +

                    "AND res.quantity >= r.quantity " +

                    "WHERE r.status = 'Pending' " +

                    "AND res.availability = 'Available' " +

                    "ORDER BY " +

                    "CASE r.priority " +
                    "WHEN 'High' THEN 1 " +
                    "WHEN 'Medium' THEN 2 " +
                    "ELSE 3 END, " +

                    "r.request_id";


            PreparedStatement ps =
                    con.prepareStatement(sql);


            ResultSet rs =
                    ps.executeQuery();


            Map<Integer, MatchData> bestMatches =
                    new LinkedHashMap<>();


            while (rs.next()) {

                Request request =
                        new Request(
                                rs.getString(
                                        "requester_name"
                                ),

                                rs.getString(
                                        "request_category"
                                ),

                                rs.getString(
                                        "request_description"
                                ),

                                rs.getInt(
                                        "request_quantity"
                                ),

                                rs.getString(
                                        "request_location"
                                )
                        );


                request.setPriority(
                        rs.getString(
                                "priority"
                        )
                );


                Resource resource =
                        new Resource(
                                rs.getString(
                                        "provider_name"
                                ),

                                rs.getString(
                                        "resource_category"
                                ),

                                rs.getString(
                                        "resource_description"
                                ),

                                rs.getInt(
                                        "resource_quantity"
                                ),

                                rs.getString(
                                        "resource_location"
                                )
                        );


                double distanceKm = -1;


                double requestLat =
                        rs.getDouble(
                                "request_latitude"
                        );

                boolean requestLatNull =
                        rs.wasNull();


                double requestLon =
                        rs.getDouble(
                                "request_longitude"
                        );

                boolean requestLonNull =
                        rs.wasNull();


                double resourceLat =
                        rs.getDouble(
                                "resource_latitude"
                        );

                boolean resourceLatNull =
                        rs.wasNull();


                double resourceLon =
                        rs.getDouble(
                                "resource_longitude"
                        );

                boolean resourceLonNull =
                        rs.wasNull();


                if (!requestLatNull &&
                        !requestLonNull &&
                        !resourceLatNull &&
                        !resourceLonNull) {

                    distanceKm =
                            calculateDistance(
                                    requestLat,
                                    requestLon,
                                    resourceLat,
                                    resourceLon
                            );
                }


                if (!MatchingEngine.isMatch(
                        request,
                        resource
                )) {

                    continue;
                }


                int score =
                        MatchingEngine.calculateScore(
                                request,
                                resource,
                                distanceKm
                        );


                MatchData match =
                        new MatchData();


                match.requestId =
                        rs.getInt(
                                "request_id"
                        );


                match.resourceId =
                        rs.getInt(
                                "resource_id"
                        );


                match.requesterName =
                        rs.getString(
                                "requester_name"
                        );


                match.requestCategory =
                        rs.getString(
                                "request_category"
                        );


                match.requestDescription =
                        rs.getString(
                                "request_description"
                        );


                match.requestQuantity =
                        rs.getInt(
                                "request_quantity"
                        );


                match.requestLocation =
                        rs.getString(
                                "request_location"
                        );


                match.priority =
                        rs.getString(
                                "priority"
                        );


                match.providerName =
                        rs.getString(
                                "provider_name"
                        );


                match.resourceDescription =
                        rs.getString(
                                "resource_description"
                        );


                match.resourceQuantity =
                        rs.getInt(
                                "resource_quantity"
                        );


                match.resourceLocation =
                        rs.getString(
                                "resource_location"
                        );


                match.distanceKm =
                        distanceKm;


                match.score =
                        score;


                MatchData existing =
                        bestMatches.get(
                                match.requestId
                        );


                if (existing == null ||
                        isBetterMatch(
                                match,
                                existing
                        )) {

                    bestMatches.put(
                            match.requestId,
                            match
                    );
                }
            }


            rs.close();
            ps.close();
            con.close();


            StringBuilder json =
                    new StringBuilder();


            json.append("[");


            boolean first = true;


            for (MatchData m :
                    bestMatches.values()) {

                if (!first) {
                    json.append(",");
                }


                first = false;


                json.append("{");


                json.append(
                        "\"requestId\":"
                )
                .append(
                        m.requestId
                )
                .append(",");


                json.append(
                        "\"resourceId\":"
                )
                .append(
                        m.resourceId
                )
                .append(",");


                json.append(
                        "\"requesterName\":\""
                )
                .append(
                        jsonEscape(
                                m.requesterName
                        )
                )
                .append("\",");


                json.append(
                        "\"requestCategory\":\""
                )
                .append(
                        jsonEscape(
                                m.requestCategory
                        )
                )
                .append("\",");


                json.append(
                        "\"requestDescription\":\""
                )
                .append(
                        jsonEscape(
                                m.requestDescription
                        )
                )
                .append("\",");


                json.append(
                        "\"requestQuantity\":"
                )
                .append(
                        m.requestQuantity
                )
                .append(",");


                json.append(
                        "\"requestLocation\":\""
                )
                .append(
                        jsonEscape(
                                m.requestLocation
                        )
                )
                .append("\",");


                json.append(
                        "\"priority\":\""
                )
                .append(
                        jsonEscape(
                                m.priority
                        )
                )
                .append("\",");


                json.append(
                        "\"providerName\":\""
                )
                .append(
                        jsonEscape(
                                m.providerName
                        )
                )
                .append("\",");


                json.append(
                        "\"resourceDescription\":\""
                )
                .append(
                        jsonEscape(
                                m.resourceDescription
                        )
                )
                .append("\",");


                json.append(
                        "\"resourceQuantity\":"
                )
                .append(
                        m.resourceQuantity
                )
                .append(",");


                json.append(
                        "\"resourceLocation\":\""
                )
                .append(
                        jsonEscape(
                                m.resourceLocation
                        )
                )
                .append("\",");


                if (m.distanceKm >= 0) {

                    json.append(
                            "\"distanceKm\":"
                    )
                    .append(
                            String.format(
                                    java.util.Locale.US,
                                    "%.2f",
                                    m.distanceKm
                            )
                    )
                    .append(",");

                } else {

                    json.append(
                            "\"distanceKm\":null,"
                    );
                }


                json.append(
                        "\"score\":"
                )
                .append(
                        m.score
                );


                json.append("}");
            }


            json.append("]");


            sendJson(
                    exchange,
                    json.toString(),
                    200
            );

        } catch (Exception e) {

            e.printStackTrace();


            sendJson(
                    exchange,
                    "{\"error\":\""
                            + jsonEscape(
                                    e.getMessage()
                            )
                            + "\"}",
                    500
            );
        }
    }

    // =====================================================
    // ALLOCATION API
    // =====================================================

    private static void handleAllocate(
            HttpExchange exchange)
            throws IOException {

        addHeaders(exchange);


        if (exchange.getRequestMethod()
                .equalsIgnoreCase("OPTIONS")) {

            exchange.sendResponseHeaders(
                    200,
                    -1
            );

            return;
        }


        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            sendJson(
                    exchange,
                    "{\"error\":\"Only POST method is allowed.\"}",
                    405
            );

            return;
        }


        Connection con = null;


        try {

            String body =
                    readBody(exchange);


            Map<String, String> data =
                    parseJson(body);


            String requestIdText =
                    data.get("requestId");


            String resourceIdText =
                    data.get("resourceId");


            String quantityText =
                    data.get("quantity");


            if (requestIdText == null ||
                    resourceIdText == null ||
                    quantityText == null) {

                sendJson(
                        exchange,
                        "{\"error\":\"Missing allocation data.\"}",
                        400
                );

                return;
            }


            int requestId =
                    Integer.parseInt(
                            requestIdText
                    );


            int resourceId =
                    Integer.parseInt(
                            resourceIdText
                    );


            int quantity =
                    Integer.parseInt(
                            quantityText
                    );


            con =
                    DBConnection.getConnection();


            if (con == null) {

                sendJson(
                        exchange,
                        "{\"error\":\"Database connection failed.\"}",
                        500
                );

                return;
            }


            con.setAutoCommit(false);


            // ================= REQUEST =================

            String requestSql =
                    "SELECT category, quantity, status " +
                    "FROM requests " +
                    "WHERE request_id = ? FOR UPDATE";


            PreparedStatement requestPs =
                    con.prepareStatement(
                            requestSql
                    );


            requestPs.setInt(
                    1,
                    requestId
            );


            ResultSet requestRs =
                    requestPs.executeQuery();


            if (!requestRs.next()) {

                con.rollback();


                sendJson(
                        exchange,
                        "{\"error\":\"Request not found.\"}",
                        404
                );

                return;
            }


            String requestCategory =
                    requestRs.getString(
                            "category"
                    );


            int requestQuantity =
                    requestRs.getInt(
                            "quantity"
                    );


            String requestStatus =
                    requestRs.getString(
                            "status"
                    );


            requestRs.close();
            requestPs.close();


            // ================= RESOURCE =================

            String resourceSql =
                    "SELECT category, quantity, availability " +
                    "FROM resources " +
                    "WHERE resource_id = ? FOR UPDATE";


            PreparedStatement resourcePs =
                    con.prepareStatement(
                            resourceSql
                    );


            resourcePs.setInt(
                    1,
                    resourceId
            );


            ResultSet resourceRs =
                    resourcePs.executeQuery();


            if (!resourceRs.next()) {

                con.rollback();


                sendJson(
                        exchange,
                        "{\"error\":\"Resource not found.\"}",
                        404
                );

                return;
            }


            String resourceCategory =
                    resourceRs.getString(
                            "category"
                    );


            int resourceQuantity =
                    resourceRs.getInt(
                            "quantity"
                    );


            String availability =
                    resourceRs.getString(
                            "availability"
                    );


            resourceRs.close();
            resourcePs.close();


            // ================= VALIDATION =================

            if (!requestStatus.equalsIgnoreCase(
                    "Pending"
            )) {

                con.rollback();


                sendJson(
                        exchange,
                        "{\"error\":\"Request is already allocated or fulfilled.\"}",
                        400
                );

                return;
            }


            if (!availability.equalsIgnoreCase(
                    "Available"
            )) {

                con.rollback();


                sendJson(
                        exchange,
                        "{\"error\":\"Resource is not available.\"}",
                        400
                );

                return;
            }


            if (!requestCategory.equalsIgnoreCase(
                    resourceCategory
            )) {

                con.rollback();


                sendJson(
                        exchange,
                        "{\"error\":\"Category mismatch.\"}",
                        400
                );

                return;
            }


            if (quantity <= 0 ||
                    quantity > requestQuantity ||
                    quantity > resourceQuantity) {

                con.rollback();


                sendJson(
                        exchange,
                        "{\"error\":\"Invalid allocation quantity.\"}",
                        400
                );

                return;
            }


            // ================= INSERT ALLOCATION =================

            String insertSql =
                    "INSERT INTO allocations " +
                    "(request_id, resource_id, allocated_quantity, allocation_status) " +
                    "VALUES (?, ?, ?, ?)";


            PreparedStatement insertPs =
                    con.prepareStatement(
                            insertSql
                    );


            insertPs.setInt(
                    1,
                    requestId
            );


            insertPs.setInt(
                    2,
                    resourceId
            );


            insertPs.setInt(
                    3,
                    quantity
            );


            insertPs.setString(
                    4,
                    "Allocated"
            );


            insertPs.executeUpdate();


            insertPs.close();


            // ================= UPDATE RESOURCE =================

            int remaining =
                    resourceQuantity - quantity;


            String newAvailability =
                    remaining == 0
                            ? "Allocated"
                            : "Available";


            String updateResourceSql =
                    "UPDATE resources " +
                    "SET quantity = ?, availability = ? " +
                    "WHERE resource_id = ?";


            PreparedStatement updateResourcePs =
                    con.prepareStatement(
                            updateResourceSql
                    );


            updateResourcePs.setInt(
                    1,
                    remaining
            );


            updateResourcePs.setString(
                    2,
                    newAvailability
            );


            updateResourcePs.setInt(
                    3,
                    resourceId
            );


            updateResourcePs.executeUpdate();


            updateResourcePs.close();


            // =================================================
            // IMPORTANT:
            // Allocation automatically completes the request
            // =================================================

            String updateRequestSql =
                    "UPDATE requests " +
                    "SET status = 'Fulfilled' " +
                    "WHERE request_id = ?";


            PreparedStatement updateRequestPs =
                    con.prepareStatement(
                            updateRequestSql
                    );


            updateRequestPs.setInt(
                    1,
                    requestId
            );


            updateRequestPs.executeUpdate();


            updateRequestPs.close();


            // ================= COMMIT =================

            con.commit();


            System.out.println(
                    "Allocation completed successfully!"
            );


            System.out.println(
                    "Request status updated to: Fulfilled"
            );


            sendJson(
                    exchange,
                    "{\"message\":\"Resource allocated successfully and request fulfilled!\"}",
                    200
            );


        } catch (Exception e) {


            try {

                if (con != null) {
                    con.rollback();
                }

            } catch (Exception ignored) {
            }


            e.printStackTrace();


            sendJson(
                    exchange,
                    "{\"error\":\""
                            + jsonEscape(
                                    e.getMessage()
                            )
                            + "\"}",
                    500
            );


        } finally {


            try {

                if (con != null) {
                    con.close();
                }

            } catch (Exception ignored) {
            }
        }
    }

    // =====================================================
    // DASHBOARD API
    // =====================================================

    private static void handleDashboard(
            HttpExchange exchange)
            throws IOException {

        addHeaders(exchange);


        if (exchange.getRequestMethod()
                .equalsIgnoreCase("OPTIONS")) {

            exchange.sendResponseHeaders(
                    200,
                    -1
            );

            return;
        }


        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("GET")) {

            sendJson(
                    exchange,
                    "{\"error\":\"Only GET method is allowed.\"}",
                    405
            );

            return;
        }


        Connection con = null;


        try {

            con =
                    DBConnection.getConnection();


            if (con == null) {

                sendJson(
                        exchange,
                        "{\"error\":\"Database connection failed.\"}",
                        500
                );

                return;
            }


            // ================= TOTAL REQUESTS =================

            String sql1 =
                    "SELECT COUNT(*) FROM requests";


            PreparedStatement ps1 =
                    con.prepareStatement(
                            sql1
                    );


            ResultSet rs1 =
                    ps1.executeQuery();


            int totalRequests = 0;


            if (rs1.next()) {

                totalRequests =
                        rs1.getInt(1);
            }


            rs1.close();
            ps1.close();


            // ================= AVAILABLE RESOURCES =================

            String sql2 =
                    "SELECT COUNT(*) " +
                    "FROM resources " +
                    "WHERE availability = 'Available'";


            PreparedStatement ps2 =
                    con.prepareStatement(
                            sql2
                    );


            ResultSet rs2 =
                    ps2.executeQuery();


            int availableResources = 0;


            if (rs2.next()) {

                availableResources =
                        rs2.getInt(1);
            }


            rs2.close();
            ps2.close();


            // ================= SUCCESSFUL MATCHES =================

            String sql3 =
                    "SELECT COUNT(DISTINCT request_id) " +
                    "FROM allocations " +
                    "WHERE allocation_status = 'Allocated'";


            PreparedStatement ps3 =
                    con.prepareStatement(
                            sql3
                    );


            ResultSet rs3 =
                    ps3.executeQuery();


            int successfulMatches = 0;


            if (rs3.next()) {

                successfulMatches =
                        rs3.getInt(1);
            }


            rs3.close();
            ps3.close();


            // ================= FULFILLED REQUESTS =================

            String sql4 =
                    "SELECT COUNT(*) " +
                    "FROM requests " +
                    "WHERE status = 'Fulfilled'";


            PreparedStatement ps4 =
                    con.prepareStatement(
                            sql4
                    );


            ResultSet rs4 =
                    ps4.executeQuery();


            int fulfilledRequests = 0;


            if (rs4.next()) {

                fulfilledRequests =
                        rs4.getInt(1);
            }


            rs4.close();
            ps4.close();


            String json =
                    "{"
                    + "\"totalRequests\":"
                    + totalRequests
                    + ","
                    + "\"availableResources\":"
                    + availableResources
                    + ","
                    + "\"successfulMatches\":"
                    + successfulMatches
                    + ","
                    + "\"fulfilledRequests\":"
                    + fulfilledRequests
                    + "}";


            sendJson(
                    exchange,
                    json,
                    200
            );


        } catch (Exception e) {

            e.printStackTrace();


            sendJson(
                    exchange,
                    "{\"error\":\""
                            + jsonEscape(
                                    e.getMessage()
                            )
                            + "\"}",
                    500
            );


        } finally {


            try {

                if (con != null) {
                    con.close();
                }

            } catch (Exception ignored) {
            }
        }
    }

    // =====================================================
    // MATCH DATA
    // =====================================================

    private static class MatchData {

        int requestId;
        int resourceId;

        String requesterName;
        String requestCategory;
        String requestDescription;
        int requestQuantity;
        String requestLocation;
        String priority;

        String providerName;
        String resourceDescription;
        int resourceQuantity;
        String resourceLocation;

        double distanceKm;

        int score;
    }

    // =====================================================
    // BETTER MATCH
    // =====================================================

    private static boolean isBetterMatch(
            MatchData newMatch,
            MatchData oldMatch) {

        if (newMatch.score >
                oldMatch.score) {

            return true;
        }


        if (newMatch.score ==
                oldMatch.score) {

            if (newMatch.distanceKm >= 0 &&
                    oldMatch.distanceKm < 0) {

                return true;
            }


            if (newMatch.distanceKm >= 0 &&
                    oldMatch.distanceKm >= 0 &&
                    newMatch.distanceKm <
                    oldMatch.distanceKm) {

                return true;
            }
        }


        return false;
    }

    // =====================================================
    // DISTANCE CALCULATION
    // =====================================================

    private static double calculateDistance(
            double lat1,
            double lon1,
            double lat2,
            double lon2) {

        final double EARTH_RADIUS_KM =
                6371.0;


        double dLat =
                Math.toRadians(
                        lat2 - lat1
                );


        double dLon =
                Math.toRadians(
                        lon2 - lon1
                );


        double a =
                Math.sin(dLat / 2) *
                Math.sin(dLat / 2)
                +
                Math.cos(
                        Math.toRadians(lat1)
                )
                *
                Math.cos(
                        Math.toRadians(lat2)
                )
                *
                Math.sin(dLon / 2) *
                Math.sin(dLon / 2);


        double c =
                2 *
                Math.atan2(
                        Math.sqrt(a),
                        Math.sqrt(1 - a)
                );


        return EARTH_RADIUS_KM * c;
    }

    // =====================================================
    // OPTIONAL DOUBLE
    // =====================================================

    private static Double parseOptionalDouble(
            String value) {

        if (value == null ||
                value.trim().isEmpty()) {

            return null;
        }


        try {

            return Double.parseDouble(
                    value
            );

        } catch (Exception e) {

            return null;
        }
    }

    // =====================================================
    // READ BODY
    // =====================================================

    private static String readBody(
            HttpExchange exchange)
            throws IOException {

        InputStream inputStream =
                exchange.getRequestBody();


        return new String(
                inputStream.readAllBytes(),
                StandardCharsets.UTF_8
        );
    }

    // =====================================================
    // JSON PARSER
    // =====================================================

    private static Map<String, String> parseJson(
            String json) {

        Map<String, String> map =
                new HashMap<>();


        json = json.trim();


        if (json.startsWith("{")) {

            json =
                    json.substring(1);
        }


        if (json.endsWith("}")) {

            json =
                    json.substring(
                            0,
                            json.length() - 1
                    );
        }


        String[] pairs =
                json.split(
                        ",(?=\\s*\")"
                );


        for (String pair : pairs) {

            String[] keyValue =
                    pair.split(
                            ":",
                            2
                    );


            if (keyValue.length == 2) {

                String key =
                        keyValue[0]
                                .trim()
                                .replace(
                                        "\"",
                                        ""
                                );


                String value =
                        keyValue[1]
                                .trim()
                                .replace(
                                        "\"",
                                        ""
                                );


                map.put(
                        key,
                        value
                );
            }
        }


        return map;
    }

    // =====================================================
    // HEADERS
    // =====================================================

    private static void addHeaders(
            HttpExchange exchange) {

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin",
                "*"
        );


        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Methods",
                "GET, POST, OPTIONS"
        );


        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Headers",
                "Content-Type"
        );
    }

    // =====================================================
    // TEXT RESPONSE
    // =====================================================

    private static void sendResponse(
            HttpExchange exchange,
            String message,
            int statusCode)
            throws IOException {

        byte[] response =
                message.getBytes(
                        StandardCharsets.UTF_8
                );


        exchange.getResponseHeaders().set(
                "Content-Type",
                "text/plain; charset=UTF-8"
        );


        exchange.sendResponseHeaders(
                statusCode,
                response.length
        );


        OutputStream output =
                exchange.getResponseBody();


        output.write(response);


        output.close();
    }

    // =====================================================
    // JSON RESPONSE
    // =====================================================

    private static void sendJson(
            HttpExchange exchange,
            String json,
            int statusCode)
            throws IOException {

        byte[] response =
                json.getBytes(
                        StandardCharsets.UTF_8
                );


        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json; charset=UTF-8"
        );


        exchange.sendResponseHeaders(
                statusCode,
                response.length
        );


        OutputStream output =
                exchange.getResponseBody();


        output.write(response);


        output.close();
    }

    // =====================================================
    // JSON ESCAPE
    // =====================================================

    private static String jsonEscape(
            String value) {

        if (value == null) {
            return "";
        }


        return value
                .replace(
                        "\\",
                        "\\\\"
                )
                .replace(
                        "\"",
                        "\\\""
                )
                .replace(
                        "\n",
                        "\\n"
                )
                .replace(
                        "\r",
                        "\\r"
                );
    }
}
