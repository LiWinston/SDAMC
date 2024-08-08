-- drop TABLE IF EXISTS test_table;
CREATE TABLE clubs (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       description TEXT NOT NULL,
                       location VARCHAR(255) NOT NULL
);
-- 插入数据到clubs表
-- 插入数据到 clubs 表
INSERT INTO clubs (name, description, location) VALUES
                                                    ('Melbourne University Soccer Club', 'A club dedicated to soccer enthusiasts and players.', 'Melbourne University Sports Centre'),
                                                    ('Melbourne University Chess Club', 'A place for chess lovers to meet and play.', 'Melbourne University Union House'),
                                                    ('Melbourne University Debate Society', 'Join us to enhance your debating skills and engage in intellectual discussions.', 'Melbourne University Arts Building'),
                                                    ('Melbourne University Photography Club', 'Explore photography and showcase your work.', 'Melbourne University Media Lab'),
                                                    ('Melbourne University Coding Club', 'For those interested in coding and technology.', 'Melbourne University Engineering Building'),
                                                    ('Melbourne University Music Society', 'A community for music lovers and performers.', 'Melbourne University Music Centre'),
                                                    ('Melbourne University Drama Club', 'For students passionate about theater and performing arts.', 'Melbourne University Drama Theatre'),
                                                    ('Melbourne University Environmental Club', 'Dedicated to environmental awareness and sustainability.', 'Melbourne University Green Room');
CREATE TABLE events (
                        id SERIAL PRIMARY KEY,
                        title VARCHAR(255) NOT NULL,
                        description TEXT NOT NULL,
                        venue VARCHAR(255) NOT NULL,
                        capacity INTEGER,
                        club_id INTEGER NOT NULL,
                        FOREIGN KEY (club_id) REFERENCES clubs(id)
);
-- 插入数据到events表
INSERT INTO events (title, description, venue, capacity, club_id) VALUES
                                                                      ('Soccer Tournament', 'An annual soccer tournament for all levels of players.', 'Melbourne University Sports Centre', 200, 1),
                                                                      ('Chess Championship', 'Join us for the annual chess championship.', 'Melbourne University Union House', 50, 2),
                                                                      ('Inter-University Debate Competition', 'Compete with other universities in debate.', 'Melbourne University Arts Building', 100, 3),
                                                                      ('Photography Exhibition', 'Showcase your photography work and view others\'.', 'Melbourne University Media Lab', 80, 4),
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
('Green Fair', 'An event to promote eco-friendly products and practices.', 'Melbourne University Green Room', 60, 8);