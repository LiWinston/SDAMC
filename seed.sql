-- Seed file for the schema

-- Insert data into clubs
INSERT INTO clubs (name, description, location)
VALUES
    ('Photography Club', 'A club for photography enthusiasts.', 'Building A'),
    ('Chess Club', 'A club for chess players of all levels.', 'Building B');

-- Insert data into students
INSERT INTO students (name, email, password)
VALUES
    ('Alice Johnson', 'alice.johnson@student.example.com', 'password123'),
    ('Bob Smith', 'bob.smith@student.example.com', 'password456'),
    ('Charlie Brown', 'charlie.brown@student.example.com', 'password789'),
    ('Dos Santos', 'santos.dos@student.example.com', 'password000');

-- Insert data into admins
INSERT INTO admins (name, email, password)
VALUES
    ('leeyc.official@gmail.com', 'leeyc.official@gmail.com', 'password123'),
    ('Alice Johnson', 'alice.johnson@student.example.com', 'password123');

-- Insert data into club_memberships
INSERT INTO club_memberships (student_id, club_id, role)
VALUES
    (1, 1, 'admin'),  -- Alice is admin of Photography Club
    (2, 2, 'normal_member'), -- Bob is a member of Chess Club
    (3, 1, 'normal_member'), -- Charlie is a member of Photography Club
    (4, 1, 'super_admin'); -- Dos is a super admin of Photography Club

-- Insert data into events
INSERT INTO events (title, description, venue, capacity, club_id, begin_time, end_time)
VALUES
    ('Photography Workshop', 'A workshop for improving photography skills.', 'Room 101', 50, 1, '2024-10-01 10:00:00', '2024-10-01 12:00:00'),
    ('Chess Tournament', 'Annual chess tournament for club members.', 'Hall B', 100, 2, '2024-11-15 14:00:00', NULL); -- A never-ending event

-- Insert data into rsvps
INSERT INTO rsvps (student_id, event_id, num_tickets)
VALUES
    (1, 1, 2), -- Alice RSVPed for Photography Workshop
    (2, 2, 1), -- Bob RSVPed for Chess Tournament
    (3, 1, 1); -- Charlie RSVPed for Photography Workshop

-- Insert data into funding_applications
INSERT INTO funding_applications (description, amount, student_id, club_id, status, semester, version)
VALUES
    ('Request for new camera equipment', 1500.00, 1, 1, 'submitted', '2024_S2', 0),
    ('Request for chess clocks', 500.00, 2, 2, 'in_review', '2024_S2', 0);
