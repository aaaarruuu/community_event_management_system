-- Step 1: Drop and Create Database
-- ============================================================================
DROP DATABASE IF EXISTS community_event_db;
CREATE DATABASE community_event_db;
USE community_event_db;

-- ============================================================================
-- Step 2: Create All Tables
-- ============================================================================

-- Table 1: users
-- ============================================================================
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(15),
    role ENUM('ADMIN', 'MEMBER') DEFAULT 'MEMBER',
    flat_number VARCHAR(20),
    profile_picture VARCHAR(255),
    bio TEXT,
    emergency_contact VARCHAR(15),
    email_notifications BOOLEAN DEFAULT TRUE,
    sms_notifications BOOLEAN DEFAULT FALSE,
    preferred_language VARCHAR(20) DEFAULT 'English',
    login_count INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_login DATETIME,
    INDEX idx_username (username),
    INDEX idx_role (role),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table 2: events
-- ============================================================================
CREATE TABLE events (
    event_id INT PRIMARY KEY AUTO_INCREMENT,
    event_name VARCHAR(200) NOT NULL,
    description TEXT,
    event_date DATE NOT NULL,
    venue VARCHAR(200) NOT NULL,
    capacity INT NOT NULL,
    organizer_id INT NOT NULL,
    category ENUM('CULTURAL', 'SPORTS', 'EDUCATIONAL', 'SOCIAL', 'HEALTH', 'RELIGIOUS') NOT NULL,
    status ENUM('UPCOMING', 'ONGOING', 'COMPLETED', 'CANCELLED') DEFAULT 'UPCOMING',
    registration_start DATE,
    registration_end DATE,
    allow_waitlist BOOLEAN DEFAULT FALSE,
    is_recurring BOOLEAN DEFAULT FALSE,
    recurrence_type VARCHAR(20),
    recurrence_interval INT DEFAULT 1,
    recurrence_end_date DATE,
    parent_event_id INT,
    created_by INT NOT NULL,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (organizer_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_date (event_date),
    INDEX idx_category (category),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table 3: representatives
-- ============================================================================
CREATE TABLE representatives (
    rep_id INT PRIMARY KEY AUTO_INCREMENT,
    rep_name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    email VARCHAR(100),
    category ENUM('Plumbing', 'Electrical', 'Maintenance', 'Cleaning', 'Security', 'Garden', 'Carpentry', 'Painting') NOT NULL,
    skill_level ENUM('Beginner', 'Intermediate', 'Expert') DEFAULT 'Intermediate',
    status ENUM('ACTIVE', 'BUSY', 'INACTIVE') DEFAULT 'ACTIVE',
    is_available BOOLEAN DEFAULT TRUE,
    rating DECIMAL(3, 2) DEFAULT 0.00,
    total_assignments INT DEFAULT 0,
    completed_assignments INT DEFAULT 0,
    avg_resolution_time DECIMAL(10, 2) DEFAULT 0.00,
    registered_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_available (is_available),
    INDEX idx_rating (rating)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table 4: issues
-- ============================================================================
CREATE TABLE issues (
    issue_id INT PRIMARY KEY AUTO_INCREMENT,
    category ENUM('Plumbing', 'Electrical', 'Maintenance', 'Cleaning', 'Security', 'Garden', 'Other') NOT NULL,
    priority ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') DEFAULT 'MEDIUM',
    description TEXT NOT NULL,
    status ENUM('PENDING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING',
    reporter_id INT NOT NULL,
    location VARCHAR(200) NOT NULL,
    reported_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    assigned_to INT,
    assigned_date DATETIME,
    resolved_date DATETIME,
    resolution TEXT,
    estimated_cost DECIMAL(10, 2),
    actual_cost DECIMAL(10, 2),
    photo_count INT DEFAULT 0,
    is_escalated BOOLEAN DEFAULT FALSE,
    escalation_reason TEXT,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    feedback TEXT,
    created_by INT NOT NULL,
    FOREIGN KEY (reporter_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_to) REFERENCES representatives(rep_id) ON DELETE SET NULL,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_status (status),
    INDEX idx_category (category),
    INDEX idx_priority (priority),
    INDEX idx_reporter (reporter_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table 5: registrations
-- ============================================================================
CREATE TABLE registrations (
    registration_id INT PRIMARY KEY AUTO_INCREMENT,
    event_id INT NOT NULL,
    user_id INT NOT NULL,
    registration_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    attendance_status ENUM('REGISTERED', 'ATTENDED', 'ABSENT', 'CANCELLED') DEFAULT 'REGISTERED',
    payment_status ENUM('PENDING', 'PAID', 'REFUNDED') DEFAULT 'PENDING',
    payment_amount DECIMAL(10, 2) DEFAULT 0.00,
    FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY unique_registration (event_id, user_id),
    INDEX idx_event (event_id),
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table 6: event_feedback
-- ============================================================================
CREATE TABLE event_feedback (
    feedback_id INT PRIMARY KEY AUTO_INCREMENT,
    event_id INT NOT NULL,
    user_id INT NOT NULL,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    is_anonymous BOOLEAN DEFAULT FALSE,
    verified BOOLEAN DEFAULT FALSE,
    helpful_count INT DEFAULT 0,
    submitted_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY unique_feedback (event_id, user_id),
    INDEX idx_event (event_id),
    INDEX idx_rating (rating)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table 7: amenities
-- ============================================================================
CREATE TABLE amenities (
    amenity_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    category ENUM('GYM', 'POOL', 'CLUBHOUSE', 'COURT', 'HALL', 'GARDEN') NOT NULL,
    capacity INT NOT NULL,
    cost_per_hour DECIMAL(10, 2) DEFAULT 0.00,
    operating_hours VARCHAR(50),
    requires_approval BOOLEAN DEFAULT FALSE,
    minimum_booking_hours INT DEFAULT 1,
    maximum_booking_hours INT DEFAULT 24,
    rating DECIMAL(3, 2) DEFAULT 0.00,
    booking_count INT DEFAULT 0,
    rules TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    INDEX idx_category (category),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table 8: amenity_bookings
-- ============================================================================
CREATE TABLE amenity_bookings (
    booking_id INT PRIMARY KEY AUTO_INCREMENT,
    amenity_id INT NOT NULL,
    user_id INT NOT NULL,
    booking_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    duration_hours DECIMAL(5, 2) NOT NULL,
    total_cost DECIMAL(10, 2) NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED') DEFAULT 'PENDING',
    is_paid BOOLEAN DEFAULT FALSE,
    payment_method VARCHAR(50),
    payment_transaction_id VARCHAR(100),
    purpose TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (amenity_id) REFERENCES amenities(amenity_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY unique_booking (amenity_id, booking_date, start_time),
    INDEX idx_date (booking_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table 9: polls
-- ============================================================================
CREATE TABLE polls (
    poll_id INT PRIMARY KEY AUTO_INCREMENT,
    question VARCHAR(500) NOT NULL,
    description TEXT,
    options JSON NOT NULL,
    created_by INT NOT NULL,
    start_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    end_date DATETIME NOT NULL,
    is_anonymous BOOLEAN DEFAULT TRUE,
    allow_multiple_choice BOOLEAN DEFAULT FALSE,
    category ENUM('GENERAL', 'EVENT', 'AMENITY', 'RULE_CHANGE', 'BUDGET') DEFAULT 'GENERAL',
    status ENUM('DRAFT', 'ACTIVE', 'CLOSED') DEFAULT 'ACTIVE',
    total_votes INT DEFAULT 0,
    results JSON,
    results_visible BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_status (status),
    INDEX idx_end_date (end_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table 10: poll_votes
-- ============================================================================
CREATE TABLE poll_votes (
    vote_id INT PRIMARY KEY AUTO_INCREMENT,
    poll_id INT NOT NULL,
    user_id INT NOT NULL,
    selected_option VARCHAR(200) NOT NULL,
    voted_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (poll_id) REFERENCES polls(poll_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY unique_vote (poll_id, user_id),
    INDEX idx_poll (poll_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table 11: announcements
-- ============================================================================
CREATE TABLE announcements (
    announcement_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    category ENUM('GENERAL', 'EVENT', 'MAINTENANCE', 'SAFETY', 'RULE') DEFAULT 'GENERAL',
    priority ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') DEFAULT 'MEDIUM',
    posted_by INT NOT NULL,
    posted_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    expiry_date DATETIME,
    is_pinned BOOLEAN DEFAULT FALSE,
    requires_acknowledgment BOOLEAN DEFAULT FALSE,
    view_count INT DEFAULT 0,
    acknowledgment_count INT DEFAULT 0,
    attachments JSON,
    target_audience ENUM('ALL', 'MEMBERS', 'ADMINS') DEFAULT 'ALL',
    FOREIGN KEY (posted_by) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_priority (priority),
    INDEX idx_date (posted_date),
    INDEX idx_pinned (is_pinned)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table 12: announcement_acknowledgments
-- ============================================================================
CREATE TABLE announcement_acknowledgments (
    ack_id INT PRIMARY KEY AUTO_INCREMENT,
    announcement_id INT NOT NULL,
    user_id INT NOT NULL,
    acknowledged_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (announcement_id) REFERENCES announcements(announcement_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY unique_ack (announcement_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table 13: activity_logs
-- ============================================================================
CREATE TABLE activity_logs (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    action_type VARCHAR(50) NOT NULL,
    action_description TEXT,
    entity_type VARCHAR(50),
    entity_id INT,
    ip_address VARCHAR(45),
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_user (user_id),
    INDEX idx_timestamp (timestamp),
    INDEX idx_action (action_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table 14: event_waitlist
-- ============================================================================
CREATE TABLE event_waitlist (
    waitlist_id INT PRIMARY KEY AUTO_INCREMENT,
    event_id INT NOT NULL,
    user_id INT NOT NULL,
    position INT NOT NULL,
    added_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    notified BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY unique_waitlist (event_id, user_id),
    INDEX idx_event (event_id),
    INDEX idx_position (position)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- Step 3: Insert Sample Data - 10 Rows Per Table
-- ============================================================================

-- Insert into users (10 users: 1 admin + 9 members)
-- ============================================================================
INSERT INTO users (username, password, full_name, email, phone, role, flat_number, bio, emergency_contact, created_date) VALUES
('aryan', MD5('aryan123'), 'Aryan Singh', 'aryan@community.com', '9876543210', 'ADMIN', 'A-101', 'System Administrator', '9876543211', NOW()),
('sachin', MD5('sachin123'), 'Sachin Verma', 'sachin@email.com', '9876543212', 'MEMBER', 'B-202', 'Database Designer', '9876543213', NOW()),
('bisanjeet', MD5('bisanjeet123'), 'Bisanjeet ', 'bisanjeet@email.com', '9876543214', 'MEMBER', 'C-303', 'Teacher', '9876543215', NOW()),
('ritik', MD5('ritik123'), 'Ritik Kumar', 'ritik@email.com', '9876543216', 'MEMBER', 'D-404', 'Doctor', '9876543217', NOW()),
('priya_singh', MD5('priya123'), 'Priya Singh', 'priya.singh@email.com', '9876543218', 'MEMBER', 'E-505', 'Architect', '9876543219', NOW()),
('amit_patel', MD5('amit123'), 'Amit Patel', 'amit.patel@email.com', '9876543220', 'MEMBER', 'F-606', 'Business Owner', '9876543221', NOW()),
('neha_gupta', MD5('neha123'), 'Neha Gupta', 'neha.gupta@email.com', '9876543222', 'MEMBER', 'G-707', 'Lawyer', '9876543223', NOW()),
('vikram_shah', MD5('vikram123'), 'Vikram Shah', 'vikram.shah@email.com', '9876543224', 'MEMBER', 'H-808', 'Consultant', '9876543225', NOW()),
('anita_verma', MD5('anita123'), 'Anita Verma', 'anita.verma@email.com', '9876543226', 'MEMBER', 'I-909', 'Designer', '9876543227', NOW()),
('rohit_jain', MD5('rohit123'), 'Rohit Jain', 'rohit.jain@email.com', '9876543228', 'MEMBER', 'J-1010', 'Accountant', '9876543229', NOW());

-- Insert into events (10 events)
-- ============================================================================
INSERT INTO events (event_name, description, event_date, venue, capacity, organizer_id, category, status, created_by) VALUES
('Yoga Session', 'Morning yoga for health and wellness', '2026-03-15', 'Community Hall', 30, 1, 'HEALTH', 'UPCOMING', 1),
('Diwali Celebration', 'Grand Diwali celebration with cultural programs', '2026-10-24', 'Main Ground', 200, 1, 'CULTURAL', 'UPCOMING', 1),
('Tech Talk Series', 'Latest trends in technology and AI', '2026-03-20', 'Conference Room', 50, 2, 'EDUCATIONAL', 'UPCOMING', 2),
('Cricket Tournament', 'Inter-block cricket championship', '2026-04-05', 'Sports Complex', 100, 3, 'SPORTS', 'UPCOMING', 3),
('Art Exhibition', 'Showcase of local artists work', '2026-03-25', 'Exhibition Hall', 80, 4, 'CULTURAL', 'UPCOMING', 4),
('Health Camp', 'Free health checkup and consultation', '2026-03-30', 'Medical Center', 150, 5, 'HEALTH', 'UPCOMING', 5),
('Music Night', 'Live music performance by local bands', '2026-04-10', 'Amphitheater', 120, 6, 'CULTURAL', 'UPCOMING', 6),
('Coding Workshop', 'Learn Python programming from scratch', '2026-04-15', 'Computer Lab', 40, 7, 'EDUCATIONAL', 'UPCOMING', 7),
('Holi Celebration', 'Traditional Holi festival with colors', '2026-03-14', 'Open Ground', 250, 1, 'CULTURAL', 'UPCOMING', 1),
('Swimming Competition', 'Annual swimming championship', '2026-04-20', 'Swimming Pool', 60, 8, 'SPORTS', 'UPCOMING', 8);

-- Insert into representatives (10 representatives)
-- ============================================================================
INSERT INTO representatives (rep_name, phone, email, category, skill_level, status, is_available, rating, total_assignments, completed_assignments) VALUES
('Ramesh Kumar', '9988776655', 'ramesh.plumber@service.com', 'Plumbing', 'Expert', 'ACTIVE', TRUE, 4.8, 45, 43),
('Suresh Electrician', '9988776656', 'suresh.electric@service.com', 'Electrical', 'Expert', 'ACTIVE', TRUE, 4.5, 38, 35),
('Ganesh Carpenter', '9988776657', 'ganesh.carpenter@service.com', 'Carpentry', 'Intermediate', 'ACTIVE', TRUE, 4.2, 25, 22),
('Mahesh Cleaner', '9988776658', 'mahesh.clean@service.com', 'Cleaning', 'Beginner', 'ACTIVE', TRUE, 4.0, 15, 14),
('Dinesh Security', '9988776659', 'dinesh.security@service.com', 'Security', 'Expert', 'ACTIVE', TRUE, 4.7, 50, 48),
('Rajesh Gardener', '9988776660', 'rajesh.garden@service.com', 'Garden', 'Intermediate', 'ACTIVE', TRUE, 4.3, 30, 28),
('Mukesh Painter', '9988776661', 'mukesh.painter@service.com', 'Painting', 'Expert', 'BUSY', FALSE, 4.6, 40, 38),
('Lokesh Maintenance', '9988776662', 'lokesh.maintenance@service.com', 'Maintenance', 'Expert', 'ACTIVE', TRUE, 4.4, 35, 33),
('Prakash Electrician', '9988776663', 'prakash.electric@service.com', 'Electrical', 'Intermediate', 'ACTIVE', TRUE, 4.1, 20, 18),
('Santosh Security', '9988776664', 'santosh.security@service.com', 'Security', 'Beginner', 'ACTIVE', TRUE, 3.9, 12, 11);

-- Insert into issues (10 issues)
-- ============================================================================
INSERT INTO issues (category, priority, description, status, reporter_id, location, assigned_to, created_by) VALUES
('Plumbing', 'HIGH', 'Water leakage in bathroom pipe', 'IN_PROGRESS', 2, 'B-202, Bathroom', 1, 2),
('Electrical', 'CRITICAL', 'Power outage in entire floor', 'PENDING', 3, 'C-Block, 3rd Floor', 2, 3),
('Maintenance', 'MEDIUM', 'Door lock needs repair', 'PENDING', 4, 'D-404, Main Door', NULL, 4),
('Cleaning', 'LOW', 'Garbage collection pending', 'COMPLETED', 5, 'E-Block, Ground Floor', 4, 5),
('Security', 'HIGH', 'Gate lock broken', 'IN_PROGRESS', 6, 'Main Gate', 5, 6),
('Garden', 'LOW', 'Lawn needs trimming', 'PENDING', 7, 'Garden Area', 6, 7),
('Plumbing', 'MEDIUM', 'Tap dripping continuously', 'PENDING', 8, 'H-808, Kitchen', 1, 8),
('Electrical', 'HIGH', 'Faulty wiring in living room', 'IN_PROGRESS', 9, 'I-909, Living Room', 2, 9),
('Maintenance', 'LOW', 'Window glass crack', 'PENDING', 10, 'J-1010, Bedroom', NULL, 10),
('Security', 'CRITICAL', 'CCTV camera not working', 'PENDING', 2, 'Parking Area', 5, 2);

-- Insert into registrations (10 registrations)
-- ============================================================================
INSERT INTO registrations (event_id, user_id, attendance_status, payment_status) VALUES
(1, 2, 'REGISTERED', 'PAID'),
(1, 3, 'REGISTERED', 'PAID'),
(1, 4, 'REGISTERED', 'PAID'),
(2, 2, 'REGISTERED', 'PAID'),
(2, 3, 'REGISTERED', 'PAID'),
(2, 5, 'REGISTERED', 'PAID'),
(3, 6, 'REGISTERED', 'PAID'),
(4, 7, 'REGISTERED', 'PAID'),
(5, 8, 'REGISTERED', 'PAID'),
(6, 9, 'REGISTERED', 'PAID');

-- Insert into event_feedback (10 feedbacks)
-- ============================================================================
INSERT INTO event_feedback (event_id, user_id, rating, comment, is_anonymous, verified) VALUES
(1, 2, 5, 'Excellent yoga session! Very relaxing and well organized.', FALSE, TRUE),
(1, 3, 4, 'Good session but venue was bit crowded.', FALSE, TRUE),
(2, 2, 5, 'Amazing Diwali celebration! Best event so far.', FALSE, TRUE),
(2, 4, 5, 'Loved the cultural programs and decorations.', FALSE, TRUE),
(3, 6, 4, 'Informative tech talk. Would love more such sessions.', FALSE, TRUE),
(4, 7, 3, 'Tournament was good but could be better organized.', FALSE, TRUE),
(5, 8, 5, 'Beautiful art exhibition. Great local talent!', FALSE, TRUE),
(6, 9, 4, 'Health camp was helpful. Got useful health tips.', FALSE, TRUE),
(1, 4, 5, 'Perfect way to start the day. Thank you!', TRUE, TRUE),
(2, 5, 4, 'Great event but parking was an issue.', FALSE, TRUE);

-- Insert into amenities (10 amenities)
-- ============================================================================
INSERT INTO amenities (name, description, category, capacity, cost_per_hour, operating_hours, requires_approval, is_active) VALUES
('Community Gym', 'Fully equipped fitness center with modern machines', 'GYM', 20, 0.00, '06:00-22:00', FALSE, TRUE),
('Swimming Pool', 'Olympic size swimming pool with changing rooms', 'POOL', 30, 50.00, '06:00-20:00', FALSE, TRUE),
('Clubhouse Main Hall', 'Large hall for events and gatherings', 'CLUBHOUSE', 100, 500.00, '08:00-23:00', TRUE, TRUE),
('Tennis Court', 'Professional tennis court with night lights', 'COURT', 4, 100.00, '06:00-21:00', FALSE, TRUE),
('Party Hall', 'Decorated hall for birthday parties', 'HALL', 50, 300.00, '10:00-23:00', TRUE, TRUE),
('Basketball Court', 'Outdoor basketball court', 'COURT', 10, 50.00, '06:00-22:00', FALSE, TRUE),
('Yoga Room', 'Peaceful room for yoga and meditation', 'GYM', 15, 0.00, '06:00-21:00', FALSE, TRUE),
('Conference Room', 'Meeting room with projector and WiFi', 'CLUBHOUSE', 25, 200.00, '09:00-20:00', TRUE, TRUE),
('Kids Play Area', 'Safe play area for children', 'GARDEN', 30, 0.00, '07:00-19:00', FALSE, TRUE),
('Badminton Court', 'Indoor badminton court', 'COURT', 4, 75.00, '06:00-21:00', FALSE, TRUE);

-- Insert into amenity_bookings (10 bookings)
-- ============================================================================
INSERT INTO amenity_bookings (amenity_id, user_id, booking_date, start_time, end_time, duration_hours, total_cost, status, purpose) VALUES
(1, 2, '2026-03-16', '07:00:00', '08:00:00', 1, 0.00, 'CONFIRMED', 'Morning workout'),
(2, 3, '2026-03-17', '15:00:00', '17:00:00', 2, 100.00, 'CONFIRMED', 'Swimming practice'),
(3, 4, '2026-03-20', '18:00:00', '22:00:00', 4, 2000.00, 'PENDING', 'Birthday party'),
(4, 5, '2026-03-18', '16:00:00', '18:00:00', 2, 200.00, 'CONFIRMED', 'Tennis match'),
(5, 6, '2026-03-25', '19:00:00', '23:00:00', 4, 1200.00, 'CONFIRMED', 'Anniversary celebration'),
(6, 7, '2026-03-19', '17:00:00', '19:00:00', 2, 100.00, 'CONFIRMED', 'Basketball practice'),
(7, 8, '2026-03-16', '06:30:00', '07:30:00', 1, 0.00, 'CONFIRMED', 'Morning yoga'),
(8, 9, '2026-03-22', '14:00:00', '16:00:00', 2, 400.00, 'PENDING', 'Team meeting'),
(9, 10, '2026-03-17', '10:00:00', '12:00:00', 2, 0.00, 'CONFIRMED', 'Kids birthday'),
(2, 2, '2026-03-21', '18:00:00', '20:00:00', 2, 100.00, 'CONFIRMED', 'Evening swim');

-- Insert into polls (10 polls)
-- ============================================================================
INSERT INTO polls (question, description, options, created_by, end_date, category, total_votes, results) VALUES
('Best time for yoga classes?', 'Help us choose the most convenient time', '["6:00 AM", "7:00 AM", "8:00 AM", "Evening 6:00 PM"]', 1, DATE_ADD(NOW(), INTERVAL 7 DAY), 'AMENITY', 15, '{"6:00 AM": 3, "7:00 AM": 8, "8:00 AM": 2, "Evening 6:00 PM": 2}'),
('Which festival to celebrate next?', 'Vote for the next community celebration', '["Holi", "Dussehra", "Christmas", "New Year"]', 1, DATE_ADD(NOW(), INTERVAL 10 DAY), 'EVENT', 25, '{"Holi": 10, "Dussehra": 5, "Christmas": 6, "New Year": 4}'),
('Swimming pool maintenance day?', 'Choose the best day for pool cleaning', '["Monday", "Tuesday", "Wednesday", "Thursday"]', 1, DATE_ADD(NOW(), INTERVAL 5 DAY), 'AMENITY', 12, '{"Monday": 2, "Tuesday": 5, "Wednesday": 3, "Thursday": 2}'),
('Security guard shift timing?', 'Preferred security patrol schedule', '["Day Shift", "Night Shift", "Both"]', 1, DATE_ADD(NOW(), INTERVAL 7 DAY), 'GENERAL', 20, '{"Day Shift": 5, "Night Shift": 7, "Both": 8}'),
('Kids play area equipment?', 'What equipment should we add', '["Swings", "Slides", "Climbing Frame", "See-Saw"]', 1, DATE_ADD(NOW(), INTERVAL 14 DAY), 'AMENITY', 18, '{"Swings": 6, "Slides": 5, "Climbing Frame": 4, "See-Saw": 3}'),
('Community newsletter frequency?', 'How often should we send newsletters', '["Weekly", "Bi-weekly", "Monthly"]', 1, DATE_ADD(NOW(), INTERVAL 7 DAY), 'GENERAL', 22, '{"Weekly": 8, "Bi-weekly": 10, "Monthly": 4}'),
('Parking rules update?', 'Should we change parking regulations', '["Yes, needs change", "No, keep current", "Neutral"]', 1, DATE_ADD(NOW(), INTERVAL 10 DAY), 'RULE_CHANGE', 30, '{"Yes, needs change": 12, "No, keep current": 15, "Neutral": 3}'),
('Gym equipment upgrade?', 'Which equipment to add first', '["Treadmill", "Cycle", "Weight Machines", "Yoga Mats"]', 1, DATE_ADD(NOW(), INTERVAL 7 DAY), 'AMENITY', 16, '{"Treadmill": 5, "Cycle": 4, "Weight Machines": 4, "Yoga Mats": 3}'),
('Annual maintenance fund?', 'Should we increase the fund', '["Yes, Increase 10%", "Yes, Increase 5%", "No Change"]', 1, DATE_ADD(NOW(), INTERVAL 15 DAY), 'BUDGET', 28, '{"Yes, Increase 10%": 8, "Yes, Increase 5%": 12, "No Change": 8}'),
('Community event frequency?', 'How many events per month', '["1 Event", "2 Events", "3 Events", "4+ Events"]', 1, DATE_ADD(NOW(), INTERVAL 7 DAY), 'EVENT', 24, '{"1 Event": 4, "2 Events": 10, "3 Events": 8, "4+ Events": 2}');

-- Insert into poll_votes (10 votes)
-- ============================================================================
INSERT INTO poll_votes (poll_id, user_id, selected_option) VALUES
(1, 2, '7:00 AM'),
(1, 3, '7:00 AM'),
(1, 4, '6:00 AM'),
(2, 2, 'Holi'),
(2, 3, 'Christmas'),
(2, 4, 'Holi'),
(3, 5, 'Tuesday'),
(4, 6, 'Both'),
(5, 7, 'Swings'),
(6, 8, 'Bi-weekly');

-- Insert into announcements (10 announcements)
-- ============================================================================
INSERT INTO announcements (title, message, category, priority, posted_by, expiry_date, is_pinned, requires_acknowledgment) VALUES
('Water Supply Interruption', 'Water supply will be interrupted on Sunday from 10 AM to 2 PM for tank cleaning. Please store water in advance.', 'MAINTENANCE', 'HIGH', 1, DATE_ADD(NOW(), INTERVAL 3 DAY), TRUE, TRUE),
('Security Alert', 'Please ensure all main gates are locked after 10 PM. Report any suspicious activity immediately.', 'SAFETY', 'CRITICAL', 1, DATE_ADD(NOW(), INTERVAL 7 DAY), TRUE, FALSE),
('Diwali Celebration Notice', 'Join us for grand Diwali celebration on Oct 24th at Main Ground. Registration open!', 'EVENT', 'MEDIUM', 1, '2026-10-23', FALSE, FALSE),
('Parking Rules Update', 'New parking stickers will be issued next week. Please collect from admin office.', 'RULE', 'MEDIUM', 1, DATE_ADD(NOW(), INTERVAL 10 DAY), FALSE, TRUE),
('Gym Maintenance', 'Community gym will be closed for maintenance on March 18th. Apologies for inconvenience.', 'MAINTENANCE', 'LOW', 1, '2026-03-18', FALSE, FALSE),
('Health Camp Reminder', 'Free health checkup camp on March 30th. Book your slot now!', 'EVENT', 'MEDIUM', 1, '2026-03-29', FALSE, FALSE),
('Electricity Bill Payment', 'Last date to pay electricity bills is March 25th. Late payments will incur penalty.', 'GENERAL', 'HIGH', 1, '2026-03-25', TRUE, TRUE),
('Swimming Pool Timings', 'Swimming pool timings changed to 6 AM - 8 PM from next week.', 'GENERAL', 'LOW', 1, DATE_ADD(NOW(), INTERVAL 30 DAY), FALSE, FALSE),
('Guest Parking Policy', 'Guest vehicles must register at security gate. Temporary passes will be issued.', 'RULE', 'MEDIUM', 1, DATE_ADD(NOW(), INTERVAL 60 DAY), FALSE, TRUE),
('Community Meeting', 'Monthly community meeting on March 28th at 6 PM in Clubhouse. All residents invited.', 'GENERAL', 'MEDIUM', 1, '2026-03-28', TRUE, FALSE);

-- Insert into announcement_acknowledgments (10 acknowledgments)
-- ============================================================================
INSERT INTO announcement_acknowledgments (announcement_id, user_id) VALUES
(1, 2),
(1, 3),
(1, 4),
(4, 2),
(4, 3),
(7, 2),
(7, 4),
(7, 5),
(9, 6),
(9, 7);

-- Insert into activity_logs (10 logs)
-- ============================================================================
INSERT INTO activity_logs (user_id, action_type, action_description, entity_type, entity_id) VALUES
(1, 'LOGIN', 'Admin logged into system', 'USER', 1),
(2, 'EVENT_REGISTER', 'Registered for Yoga Session', 'EVENT', 1),
(3, 'ISSUE_REPORT', 'Reported electrical issue', 'ISSUE', 2),
(4, 'AMENITY_BOOK', 'Booked clubhouse for party', 'AMENITY', 3),
(5, 'POLL_VOTE', 'Voted in swimming pool poll', 'POLL', 3),
(6, 'ANNOUNCEMENT_ACK', 'Acknowledged water supply notice', 'ANNOUNCEMENT', 1),
(2, 'EVENT_REGISTER', 'Registered for Diwali event', 'EVENT', 2),
(7, 'FEEDBACK_SUBMIT', 'Submitted feedback for cricket tournament', 'EVENT', 4),
(8, 'AMENITY_BOOK', 'Booked yoga room', 'AMENITY', 7),
(9, 'ISSUE_REPORT', 'Reported maintenance issue', 'ISSUE', 9);

-- Insert into event_waitlist (10 waitlist entries)
-- ============================================================================
INSERT INTO event_waitlist (event_id, user_id, position, notified) VALUES
(3, 8, 1, FALSE),
(3, 9, 2, FALSE),
(3, 10, 3, FALSE),
(8, 2, 1, FALSE),
(8, 3, 2, FALSE),
(8, 4, 3, FALSE),
(10, 5, 1, FALSE),
(10, 6, 2, FALSE),
(10, 7, 3, FALSE),
(4, 9, 1, FALSE);

-- ============================================================================
-- Step 4: Verification Queries
-- ============================================================================

-- Count rows in each table
SELECT 'users' as TableName, COUNT(*) as RowCount FROM users
UNION ALL
SELECT 'events', COUNT(*) FROM events
UNION ALL
SELECT 'representatives', COUNT(*) FROM representatives
UNION ALL
SELECT 'issues', COUNT(*) FROM issues
UNION ALL
SELECT 'registrations', COUNT(*) FROM registrations
UNION ALL
SELECT 'event_feedback', COUNT(*) FROM event_feedback
UNION ALL
SELECT 'amenities', COUNT(*) FROM amenities
UNION ALL
SELECT 'amenity_bookings', COUNT(*) FROM amenity_bookings
UNION ALL
SELECT 'polls', COUNT(*) FROM polls
UNION ALL
SELECT 'poll_votes', COUNT(*) FROM poll_votes
UNION ALL
SELECT 'announcements', COUNT(*) FROM announcements
UNION ALL
SELECT 'announcement_acknowledgments', COUNT(*) FROM announcement_acknowledgments
UNION ALL
SELECT 'activity_logs', COUNT(*) FROM activity_logs
UNION ALL
SELECT 'event_waitlist', COUNT(*) FROM event_waitlist;

-- ============================================================================
-- SUCCESS MESSAGE
-- ============================================================================
SELECT '============================================' as Message
UNION ALL
SELECT 'DATABASE CREATED SUCCESSFULLY!'
UNION ALL
SELECT 'Total Tables: 14'
UNION ALL
SELECT 'Total Sample Data Rows: 140 (10 per table)'
UNION ALL
SELECT '============================================'
UNION ALL
SELECT 'Test Credentials:'
UNION ALL
SELECT 'Username: admin | Password: admin123'
UNION ALL
SELECT 'Username: john_doe | Password: john123'
UNION ALL
SELECT '============================================';