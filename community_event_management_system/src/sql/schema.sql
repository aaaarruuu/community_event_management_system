-- Community Event Management System Database Schema

DROP DATABASE IF EXISTS community_events_db;
CREATE DATABASE community_events_db;
USE community_events_db;

-- Users Table
CREATE TABLE Users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role ENUM('Admin', 'Member') DEFAULT 'Member',
    contact VARCHAR(15),
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Events Table
CREATE TABLE Events (
    event_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    event_date DATE NOT NULL,
    event_time TIME NOT NULL,
    description TEXT,
    venue VARCHAR(200),
    organizer VARCHAR(100),
    image_path VARCHAR(300),
    created_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES Users(user_id)
);

-- Representatives Table
CREATE TABLE Representatives (
    rep_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    category ENUM('Plumber', 'Electrician', 'Mechanic', 'Gardener', 'Cleaner', 'Other') NOT NULL,
    contact VARCHAR(15) NOT NULL,
    email VARCHAR(100),
    image_path VARCHAR(300),
    status ENUM('Available', 'Busy', 'Unavailable') DEFAULT 'Available',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Issues Table
CREATE TABLE Issues (
    issue_id INT PRIMARY KEY AUTO_INCREMENT,
    category ENUM('Water Leakage', 'Electrical Problem', 'Road Damage', 'Garbage/Bins', 'Structural Issue', 'Other') NOT NULL,
    description TEXT NOT NULL,
    location VARCHAR(200) NOT NULL,
    image_path VARCHAR(300),
    reporter_id INT NOT NULL,
    date_reported TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('Pending', 'In-Progress', 'Completed', 'Cancelled') DEFAULT 'Pending',
    priority ENUM('Low', 'Medium', 'High', 'Critical') DEFAULT 'Medium',
    FOREIGN KEY (reporter_id) REFERENCES Users(user_id)
);

-- Assignments Table
CREATE TABLE Assignments (
    assign_id INT PRIMARY KEY AUTO_INCREMENT,
    issue_id INT NOT NULL,
    rep_id INT NOT NULL,
    assigned_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completion_date TIMESTAMP NULL,
    status ENUM('Assigned', 'In-Progress', 'Completed', 'Cancelled') DEFAULT 'Assigned',
    notes TEXT,
    FOREIGN KEY (issue_id) REFERENCES Issues(issue_id) ON DELETE CASCADE,
    FOREIGN KEY (rep_id) REFERENCES Representatives(rep_id)
);

-- Insert Sample Data

-- Users
INSERT INTO Users (username, password, role, contact, email) VALUES
('admin', 'admin123', 'Admin', '9876543210', 'admin@community.com'),
('john_doe', 'pass123', 'Member', '9876543211', 'john@email.com'),
('jane_smith', 'pass123', 'Member', '9876543212', 'jane@email.com'),
('mike_wilson', 'pass123', 'Member', '9876543213', 'mike@email.com');

-- Representatives
INSERT INTO Representatives (name, category, contact, email, status) VALUES
('Rajesh Kumar', 'Plumber', '9988776655', 'rajesh.plumber@service.com', 'Available'),
('Amit Sharma', 'Electrician', '9988776656', 'amit.electric@service.com', 'Available'),
('Suresh Patel', 'Mechanic', '9988776657', 'suresh.mechanic@service.com', 'Available'),
('Ramesh Verma', 'Plumber', '9988776658', 'ramesh.plumber@service.com', 'Available'),
('Vijay Singh', 'Electrician', '9988776659', 'vijay.electric@service.com', 'Busy'),
('Ankit Gupta', 'Gardener', '9988776660', 'ankit.garden@service.com', 'Available'),
('Deepak Joshi', 'Cleaner', '9988776661', 'deepak.clean@service.com', 'Available');

-- Events
INSERT INTO Events (title, event_date, event_time, description, venue, organizer, created_by) VALUES
('Community Clean-Up Drive', '2026-02-15', '09:00:00', 'Annual community cleaning event. Join us to make our locality cleaner and greener!', 'Community Park', 'Green Committee', 1),
('Annual Sports Day', '2026-03-10', '08:00:00', 'Fun-filled sports activities for all age groups. Prizes for winners!', 'Sports Complex', 'Sports Committee', 1),
('Health Checkup Camp', '2026-02-20', '10:00:00', 'Free health checkup camp by City Hospital. All members are welcome.', 'Community Hall', 'Health Committee', 1),
('Cultural Festival', '2026-04-05', '18:00:00', 'Celebrate diversity with music, dance, and food from different cultures.', 'Main Auditorium', 'Cultural Committee', 1),
('Kids Workshop - Art & Craft', '2026-02-25', '15:00:00', 'Creative workshop for kids aged 5-12. All materials provided.', 'Activity Center', 'Education Committee', 1);

-- Issues
INSERT INTO Issues (category, description, location, reporter_id, status, priority) VALUES
('Water Leakage', 'Severe water leakage from overhead tank causing damage to walls', 'Block A, Flat 101', 2, 'Pending', 'High'),
('Electrical Problem', 'Street light not working for past 3 days near main gate', 'Main Gate Area', 3, 'Pending', 'Medium'),
('Road Damage', 'Large pothole causing difficulty for vehicles', 'Internal Road near Block B', 2, 'In-Progress', 'High'),
('Garbage/Bins', 'Garbage bins overflowing, need immediate attention', 'Block C Common Area', 4, 'Pending', 'Medium'),
('Electrical Problem', 'Power fluctuation in Block D causing appliance damage', 'Block D', 3, 'Pending', 'Critical');

-- Assignments
INSERT INTO Assignments (issue_id, rep_id, status, notes) VALUES
(1, 1, 'Assigned', 'Assigned to Rajesh Kumar for inspection'),
(2, 2, 'Assigned', 'Electrician assigned to fix street light'),
(3, 3, 'In-Progress', 'Road repair work started'),
(5, 5, 'Assigned', 'High priority - power issue investigation');

-- Create Views for Dashboard
CREATE VIEW dashboard_stats AS
SELECT
    (SELECT COUNT(*) FROM Issues WHERE status = 'Pending') as pending_issues,
    (SELECT COUNT(*) FROM Issues WHERE status = 'In-Progress') as inprogress_issues,
    (SELECT COUNT(*) FROM Issues WHERE status = 'Completed') as completed_issues,
    (SELECT COUNT(*) FROM Events WHERE event_date >= CURDATE()) as upcoming_events,
    (SELECT COUNT(*) FROM Events WHERE event_date < CURDATE()) as past_events,
    (SELECT COUNT(*) FROM Representatives WHERE status = 'Available') as available_reps;

-- Create View for Issue Details
CREATE VIEW issue_details AS
SELECT
    i.issue_id,
    i.category,
    i.description,
    i.location,
    i.date_reported,
    i.status as issue_status,
    i.priority,
    u.username as reporter_name,
    u.contact as reporter_contact,
    a.assign_id,
    a.status as assignment_status,
    r.name as rep_name,
    r.category as rep_category,
    r.contact as rep_contact
FROM Issues i
LEFT JOIN Users u ON i.reporter_id = u.user_id
LEFT JOIN Assignments a ON i.issue_id = a.issue_id
LEFT JOIN Representatives r ON a.rep_id = r.rep_id;