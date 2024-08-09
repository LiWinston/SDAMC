drop TABLE IF EXISTS clubs, events, students, club_memberships, club_admins, rsvps;
-- 创建 clubs 表
CREATE TABLE clubs (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       description TEXT NOT NULL,
                       location VARCHAR(255) NOT NULL
);

-- 插入数据到 clubs 表
INSERT INTO clubs (name, description, location) VALUES
                                                    ('Melbourne University Soccer Club', 'A club dedicated to soccer enthusiasts and players.', 'Melbourne University Sports Centre'),
                                                    ('Melbourne University Chess Club', 'A place for chess lovers to meet and play.', 'Melbourne University Union House'),
                                                    ('Melbourne University Debate Society', 'Join us to enhance your debating skills and engage in intellectual discussions.', 'Melbourne University Arts Building'),
                                                    ('Melbourne University Photography Club', 'Explore photography and showcase your work.', 'Melbourne University Media Lab'),
                                                    ('Melbourne University Coding Club', 'For those interested in coding and technology.', 'Melbourne University Engineering Building'),
                                                    ('Melbourne University Music Society', 'A community for music lovers and performers.', 'Melbourne University Music Centre'),
                                                    ('Melbourne University Drama Club', 'For students passionate about theater and performing arts.', 'Melbourne University Drama Theatre'),
                                                    ('Melbourne University Environmental Club', 'Dedicated to environmental awareness and sustainability.', 'Melbourne University Green Room'),
                                                    ('Melbourne University Dance Club', 'A club for dance enthusiasts to learn and perform.', 'Melbourne University Sports Centre'),
                                                    ('Melbourne University Culinary Club', 'For those who love cooking and want to share their passion.', 'Melbourne University Union House'),
                                                    ('Melbourne University Robotics Club', 'Explore robotics and participate in competitions.', 'Melbourne University Engineering Building'),
                                                    ('Melbourne University Book Club', 'A place for book lovers to discuss and share their favorite reads.', 'Melbourne University Media Lab');

-- 创建 events 表
CREATE TABLE events (
                        id SERIAL PRIMARY KEY,
                        title VARCHAR(255) NOT NULL,
                        description TEXT NOT NULL,
                        venue VARCHAR(255) NOT NULL,
                        capacity INTEGER,
                        club_id INTEGER NOT NULL,
                        FOREIGN KEY (club_id) REFERENCES clubs(id)
);

-- 插入数据到 events 表
INSERT INTO events (title, description, venue, capacity, club_id) VALUES
                                                                      ('Soccer Tournament', 'An annual soccer tournament for all levels of players.', 'Melbourne University Sports Centre', 200, 1),
                                                                      ('Chess Championship', 'Join us for the annual chess championship.', 'Melbourne University Union House', 50, 2),
                                                                      ('Inter-University Debate Competition', 'Compete with other universities in debate.', 'Melbourne University Arts Building', 100, 3),
                                                                      ('Photography Exhibition', 'Showcase your photography work and view others\.', 'Melbourne University Media Lab', 80, 4),
                                                                      ('Hackathon 2024', 'A 24-hour coding event with challenges and prizes.', 'Melbourne University Engineering Building', 150, 5),
                                                                      ('Music Jam Session', 'A casual session for music lovers to perform and collaborate.', 'Melbourne University Music Centre', 60, 6),
                                                                      ('Drama Performance Night', 'Watch or participate in a night of drama performances.', 'Melbourne University Drama Theatre', 120, 7),
                                                                      ('Sustainability Workshop', 'Learn about sustainable practices and how to make a difference.', 'Melbourne University Green Room', 40, 8),
                                                                      ('Soccer Training Camp', 'Weekly training sessions for soccer enthusiasts.', 'Melbourne University Sports Centre', 100, 1),
                                                                      ('Chess Open Day', 'An open day for anyone interested in chess to come and play.', 'Melbourne University Union House', 30, 2),
                                                                      ('Debate Workshop', 'Improve your debating skills with expert guidance.', 'Melbourne University Arts Building', 50, 3),
                                                                      ('Photography Workshop', 'Learn new techniques and improve your photography skills.', 'Melbourne University Media Lab', 40, 4),
                                                                      ('Tech Talks', 'Weekly talks on various technology topics and trends.', 'Melbourne University Engineering Building', 80, 5),
                                                                      ('Open Mic Night', 'An evening of performances, from music to spoken word.', 'Melbourne University Music Centre', 70, 6),
                                                                      ('Theater Workshop', 'Explore different aspects of theater and acting.', 'Melbourne University Drama Theatre', 50, 7),
                                                                      ('Green Fair', 'An event to promote eco-friendly products and practices.', 'Melbourne University Green Room', 60, 8),
                                                                      ('Dance Workshop', 'A workshop to learn and practice various dance styles.', 'Melbourne University Sports Centre', 50, 9), -- 对应 Dance Club
                                                                      ('Culinary Masterclass', 'An advanced cooking class with a professional chef.', 'Melbourne University Union House', 20, 10), -- 对应 Culinary Club
                                                                      ('Robotics Competition', 'A competition to showcase and test your robotics skills.', 'Melbourne University Engineering Building', 40, 11), -- 对应 Robotics Club
                                                                      ('Book Reading Session', 'A session to discuss and share thoughts on a selected book.', 'Melbourne University Media Lab', 30, 12); -- 对应 Book Club



CREATE TABLE students (
                          id SERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          email VARCHAR(255) NOT NULL UNIQUE
);

INSERT INTO students (name, email) VALUES
                                       ('Alice Johnson', 'alice.johnson@student.unimelb.edu.au'),
                                       ('Bob Smith', 'bob.smith@student.unimelb.edu.au'),
                                       ('Charlie Brown', 'charlie.brown@student.unimelb.edu.au'),
                                       ('Diana King', 'diana.king@student.unimelb.edu.au'),
                                       ('Edward Lee', 'edward.lee@student.unimelb.edu.au'),
                                       ('Fiona White', 'fiona.white@student.unimelb.edu.au'),
                                       ('George Miller', 'george.miller@student.unimelb.edu.au'),
                                       ('Hannah Scott', 'hannah.scott@student.unimelb.edu.au'),
                                       ('Ivan Davis', 'ivan.davis@student.unimelb.edu.au'),
                                       ('Julia Adams', 'julia.adams@student.unimelb.edu.au'),
                                       ('Kevin Brown', 'kevin.brown@student.unimelb.edu.au'),
                                       ('Laura Green', 'laura.green@student.unimelb.edu.au'),
                                       ('Michael Harris', 'michael.harris@student.unimelb.edu.au'),
                                       ('Nina Walker', 'nina.walker@student.unimelb.edu.au'),
                                       ('Oliver Young', 'oliver.young@student.unimelb.edu.au'),
                                       ('Paula Edwards', 'paula.edwards@student.unimelb.edu.au'),
                                       ('Quincy Taylor', 'quincy.taylor@student.unimelb.edu.au'),
                                       ('Rachel Lewis', 'rachel.lewis@student.unimelb.edu.au'),
                                       ('Steve Anderson', 'steve.anderson@student.unimelb.edu.au'),
                                       ('Tina Martinez', 'tina.martinez@student.unimelb.edu.au');


CREATE TABLE club_memberships (
                                  student_id INTEGER NOT NULL,
                                  club_id INTEGER NOT NULL,
                                  PRIMARY KEY (student_id, club_id),
                                  FOREIGN KEY (student_id) REFERENCES students(id),
                                  FOREIGN KEY (club_id) REFERENCES clubs(id)
);


INSERT INTO club_memberships (student_id, club_id) VALUES
                                                       (1, 1),  -- Alice Johnson joins the Soccer Club
                                                       (2, 2),  -- Bob Smith joins the Chess Club
                                                       (3, 3),  -- Charlie Brown joins the Debate Society
                                                       (4, 4),  -- Diana King joins the Photography Club
                                                       (5, 5),  -- Edward Lee joins the Coding Club
                                                       (6, 6),  -- Fiona White joins the Music Society
                                                       (7, 7),  -- George Miller joins the Drama Club
                                                       (8, 8),  -- Hannah Scott joins the Environmental Club
                                                       (9, 1),  -- Ivan Davis also joins the Soccer Club
                                                       (10, 2), -- Julia Adams also joins the Chess Club
                                                       (11, 3), -- Kevin Brown also joins the Debate Society
                                                       (12, 4), -- Laura Green also joins the Photography Club
                                                       (13, 5), -- Michael Harris also joins the Coding Club
                                                       (14, 6), -- Nina Walker also joins the Music Society
                                                       (15, 7), -- Oliver Young also joins the Drama Club
                                                       (16, 8), -- Paula Edwards also joins the Environmental Club
                                                       (17, 9), -- Quincy Taylor joins the Dance Club
                                                       (18, 10),-- Rachel Lewis joins the Culinary Club
                                                       (19, 11),-- Steve Anderson joins the Robotics Club
                                                       (20, 12);-- Tina Martinez joins the Book Club



CREATE TABLE club_admins (
                             student_id INTEGER NOT NULL,
                             club_id INTEGER NOT NULL,
                             PRIMARY KEY (student_id, club_id),
                             FOREIGN KEY (student_id) REFERENCES students(id),
                             FOREIGN KEY (club_id) REFERENCES clubs(id)
);


INSERT INTO club_admins (student_id, club_id) VALUES
                                                  (1, 1),  -- Alice Johnson is the admin of the Soccer Club
                                                  (2, 2),  -- Bob Smith is the admin of the Chess Club
                                                  (3, 3),  -- Charlie Brown is the admin of the Debate Society
                                                  (4, 4),  -- Diana King is the admin of the Photography Club
                                                  (5, 5),  -- Edward Lee is the admin of the Coding Club
                                                  (6, 6),  -- Fiona White is the admin of the Music Society
                                                  (7, 7),  -- George Miller is the admin of the Drama Club
                                                  (8, 8),  -- Hannah Scott is the admin of the Environmental Club
                                                  (17, 9), -- Quincy Taylor is the admin of the Dance Club
                                                  (18, 10),-- Rachel Lewis is the admin of the Culinary Club
                                                  (19, 11),-- Steve Anderson is the admin of the Robotics Club
                                                  (20, 12);-- Tina Martinez is the admin of the Book Club



CREATE TABLE rsvps (
                       id SERIAL PRIMARY KEY,
                       student_id INTEGER NOT NULL,
                       event_id INTEGER NOT NULL,
                       num_tickets INTEGER NOT NULL,
                       FOREIGN KEY (student_id) REFERENCES students(id),
                       FOREIGN KEY (event_id) REFERENCES events(id)
);



INSERT INTO rsvps (student_id, event_id, num_tickets) VALUES
                                                          (1, 1, 1),   -- Alice Johnson RSVPs for the Soccer Tournament
                                                          (2, 2, 1),   -- Bob Smith RSVPs for the Chess Championship
                                                          (3, 3, 2),   -- Charlie Brown RSVPs for the Debate Competition
                                                          (4, 4, 1),   -- Diana King RSVPs for the Photography Exhibition
                                                          (5, 5, 3),   -- Edward Lee RSVPs for the Hackathon
                                                          (6, 6, 1),   -- Fiona White RSVPs for the Music Jam Session
                                                          (7, 7, 2),   -- George Miller RSVPs for the Drama Performance Night
                                                          (8, 8, 1),   -- Hannah Scott RSVPs for the Sustainability Workshop
                                                          (9, 1, 1),   -- Ivan Davis RSVPs for the Soccer Tournament
                                                          (10, 2, 1),  -- Julia Adams RSVPs for the Chess Open Day
                                                          (11, 3, 2),  -- Kevin Brown RSVPs for the Debate Workshop
                                                          (12, 4, 1),  -- Laura Green RSVPs for the Photography Workshop
                                                          (13, 5, 2),  -- Michael Harris RSVPs for the Tech Talks
                                                          (14, 6, 1),  -- Nina Walker RSVPs for the Open Mic Night
                                                          (15, 7, 1),  -- Oliver Young RSVPs for the Theater Workshop
                                                          (16, 8, 1),  -- Paula Edwards RSVPs for the Green Fair
                                                          (17, 9, 1),  -- Quincy Taylor RSVPs for the Dance Workshop
                                                          (18, 10, 2), -- Rachel Lewis RSVPs for the Culinary Masterclass
                                                          (19, 11, 1), -- Steve Anderson RSVPs for the Robotics Competition
                                                          (20, 12, 1); -- Tina Martinez RSVPs for the Book Reading Session