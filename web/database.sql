CREATE DATABASE community_resource_db;

USE community_resource_db;

CREATE TABLE requests (
    request_id INT AUTO_INCREMENT PRIMARY KEY,
    requester_name VARCHAR(100),
    category VARCHAR(50),
    description VARCHAR(255),
    quantity INT,
    location VARCHAR(100),
    priority VARCHAR(20),
    status VARCHAR(30) DEFAULT 'Pending'
);

CREATE TABLE resources (
    resource_id INT AUTO_INCREMENT PRIMARY KEY,
    provider_name VARCHAR(100),
    category VARCHAR(50),
    description VARCHAR(255),
    quantity INT,
    location VARCHAR(100),
    availability VARCHAR(30) DEFAULT 'Available'
);

CREATE TABLE allocations (
    allocation_id INT AUTO_INCREMENT PRIMARY KEY,
    request_id INT,
    resource_id INT,
    allocated_quantity INT,
    allocation_status VARCHAR(30) DEFAULT 'Allocated',

    FOREIGN KEY (request_id)
        REFERENCES requests(request_id),

    FOREIGN KEY (resource_id)
        REFERENCES resources(resource_id)
);