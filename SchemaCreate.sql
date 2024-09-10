drop TABLE IF EXISTS clubs, events, students, club_memberships, club_admins, rsvps;

-- 创建 clubs 表
CREATE TABLE clubs (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    location VARCHAR(255) NOT NULL
);

-- 创建 events 表
CREATE TABLE events (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    venue VARCHAR(255) NOT NULL,
    capacity INTEGER,
    club_id INTEGER NOT NULL,
    begin_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,  -- Allow to be null, null represent a never end event
    FOREIGN KEY (club_id) REFERENCES clubs(id)
);
CREATE INDEX idx_events_begin_time ON events (begin_time);
CREATE INDEX idx_events_end_time ON events (end_time);

CREATE TABLE students (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TYPE member_role AS ENUM ('normal_member', 'admin');
CREATE TABLE club_memberships (
    id SERIAL PRIMARY KEY,
    student_id INTEGER NOT NULL,
    club_id INTEGER NOT NULL,
    role member_role NOT NULL DEFAULT 'normal_member',
    
    CONSTRAINT unique_student_club UNIQUE (student_id, club_id),

    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (club_id) REFERENCES clubs(id)
);
CREATE INDEX idx_club_memberships_student_club ON club_memberships (student_id, club_id);

CREATE TABLE rsvps (
    id SERIAL PRIMARY KEY,
    student_id INTEGER NOT NULL,
    event_id INTEGER NOT NULL,
    num_tickets INTEGER NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (event_id) REFERENCES events(id)
);
CREATE INDEX rsvps_sid_eid_hash ON rsvps (student_id, event_id);
CREATE INDEX rsvps_sid_hash ON rsvps (student_id);
CREATE INDEX rsvps_eid_hash ON rsvps (event_id);