CREATE DATABASE IF NOT EXISTS gallo;

USE gallo;

CREATE TABLE IF NOT EXISTS person(
    id bigint NOT NULL,
    first_name varchar(45) NOT NULL,
    last_name varchar(45) NOT NULL,
    phone_number varchar(45) NOT NULL,
    birth_date DATE NOT NULL,
    gender varchar(2) NOT NULL,
    height decimal(4,2) DEFAULT NULL,
    weight decimal(5,2) DEFAULT NULL
);

ALTER TABLE person ADD PRIMARY KEY (id);
ALTER TABLE person MODIFY id bigint NOT NULL AUTO_INCREMENT;

ALTER TABLE person ADD CONSTRAINT chk_person_gender CHECK (gender IN ('M', 'F', 'NT'));

ALTER TABLE person ADD UNIQUE INDEX uq_person_phone_number (phone_number ASC) VISIBLE;

CREATE TABLE IF NOT EXISTS account(
    id bigint NOT NULL,
    email varchar(100) NOT NULL,
    password varchar (150) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME DEFAULT NULL,
    enabled boolean NOT NULL DEFAULT 1,
    person_id bigint NOT NULL
);

ALTER TABLE account ADD PRIMARY KEY (id);
ALTER TABLE account MODIFY id bigint NOT NULL AUTO_INCREMENT;

ALTER TABLE account ADD UNIQUE INDEX uq_account_email (email ASC) VISIBLE;
ALTER TABLE account ADD CONSTRAINT fk_account_person_id FOREIGN KEY (person_id) REFERENCES person(id);
ALTER TABLE account ADD UNIQUE INDEX uq_fk_account_person_id (person_id ASC) VISIBLE;

CREATE TABLE IF NOT EXISTS role(
    id bigint NOT NULL,
    name varchar(45) NOT NULL
);

ALTER TABLE role ADD PRIMARY KEY (id);
ALTER TABLE role MODIFY id bigint NOT NULL AUTO_INCREMENT;
ALTER TABLE role ADD UNIQUE INDEX uq_role_name (name ASC) VISIBLE;

CREATE TABLE IF NOT EXISTS account_role(
    account_id bigint NOT NULL,
    role_id bigint NOT NULL
);

ALTER TABLE account_role ADD PRIMARY KEY(account_id, role_id);

ALTER TABLE account_role ADD CONSTRAINT fk_account_role_account_id 
FOREIGN KEY (account_id) REFERENCES account(id);
ALTER TABLE account_role ADD CONSTRAINT fk_account_role_role_id 
FOREIGN KEY (role_id) REFERENCES role(id);

CREATE TABLE IF NOT EXISTS body_part(
    id bigint NOT NULL,
    name varchar(45) NOT NULL
);

ALTER TABLE body_part ADD PRIMARY KEY (id);
ALTER TABLE body_part MODIFY id bigint NOT NULL AUTO_INCREMENT;
ALTER TABLE body_part ADD UNIQUE INDEX uq_body_part_name (name ASC) VISIBLE;

CREATE TABLE IF NOT EXISTS exercise(
    id bigint NOT NULL,
    name varchar(60) NOT NULL,
    description varchar(200) DEFAULT NULL,
    weight_required boolean NOT NULL DEFAULT 0,
    image_url varchar(255) DEFAULT NULL
);

ALTER TABLE exercise ADD PRIMARY KEY (id);
ALTER TABLE exercise MODIFY id bigint NOT NULL AUTO_INCREMENT;


CREATE TABLE IF NOT EXISTS exercise_body_part(
    exercise_id bigint NOT NULL,
    body_part_id bigint NOT NULL
);

ALTER TABLE exercise_body_part ADD PRIMARY KEY(exercise_id,body_part_id);

ALTER TABLE exercise_body_part ADD CONSTRAINT fk_exercise_body_part_exercise_id
FOREIGN KEY (exercise_id) REFERENCES exercise(id);
ALTER TABLE exercise_body_part ADD CONSTRAINT fk_exercise_body_part_id
FOREIGN KEY (body_part_id) REFERENCES body_part(id);


CREATE TABLE IF NOT EXISTS workout(
    id bigint NOT NULL,
    workout_date date NOT NULL,
    workout_length decimal(4,2) DEFAULT NULL,
    exercise_id bigint NOT NULL,
    account_id bigint NOT NULL
);

ALTER TABLE workout ADD PRIMARY KEY (id);
ALTER TABLE workout MODIFY id bigint NOT NULL AUTO_INCREMENT;

ALTER TABLE workout ADD CONSTRAINT fk_workout_exercise_id 
FOREIGN KEY (exercise_id) REFERENCES exercise(id);

ALTER TABLE workout ADD CONSTRAINT fk_workout_account_id 
FOREIGN KEY (account_id) REFERENCES account(id);

CREATE TABLE IF NOT EXISTS workout_set(
    id bigint NOT NULL,
    rep_amount int (2) NOT NULL,
    weight_amount decimal(5,2) NOT NULL,
    to_failure boolean NOT NULL DEFAULT 0,
    workout_id bigint NOT NULL
);

ALTER TABLE workout_set ADD PRIMARY KEY (id);
ALTER TABLE workout_set MODIFY id bigint NOT NULL AUTO_INCREMENT;

ALTER TABLE workout_set ADD CONSTRAINT fk_workout_set_workout_id
FOREIGN KEY (workout_id) REFERENCES workout(id);


INSERT INTO role(name) VALUES
('ROLE_ADMIN'),
('ROLE_USER');

-- Insert body parts
INSERT INTO body_part (name) VALUES
('Abdominals'),
('Chest'),
('Triceps'),
('Shoulders'),
('Deltoids'),
('Back'),
('Biceps'),
('Forearms'),
('Glutes'),
('Quadriceps'),
('Hamstrings'),
('Calves');

-- Insert exercises with weight_required (1 = TRUE, 0 = FALSE)
INSERT INTO exercise (name, weight_required) VALUES
('Leg Raises', 0),
('Ab Crunch Machine', 1),
('Leg Press', 1),
('Squats', 1),
('Leg Extension', 1),
('Leg Curl', 1),
('Hip Thrust', 1),
('Calf Raise on Leg Press', 1),

('Dips', 0),
('Incline Dumbbell Press', 1),
('Overhead Triceps Extension', 1),
('Triceps Pulldown', 1),
('Cable Fly', 1),
('Shoulder Press Machine', 1),
('Rear Deltoid Machine', 1),

('Push-ups', 0),
('Lat Pulldown', 1),
('Seated Machine Row', 1),
('Biceps Curl Machine', 1),
('EZ Bar Curl', 1),
('EZ Bar Reverse Curl', 1),
('Shrugs with Barbell', 1),
('Pinch Plates', 1),
('Wrist Curl Dumbbell seated', 1);

-- Insert relations between exercises and body parts
-- Abdominals (id=1)
INSERT INTO exercise_body_part VALUES (1, 1);
INSERT INTO exercise_body_part VALUES (2, 1);

-- Legs (Quadriceps=10, Hamstrings=11, Glutes=9, Calves=12)
INSERT INTO exercise_body_part VALUES (3, 10);
INSERT INTO exercise_body_part VALUES (3, 11);
INSERT INTO exercise_body_part VALUES (3, 9);
INSERT INTO exercise_body_part VALUES (3, 12);

INSERT INTO exercise_body_part VALUES (4, 10);
INSERT INTO exercise_body_part VALUES (4, 11);
INSERT INTO exercise_body_part VALUES (4, 9);

INSERT INTO exercise_body_part VALUES (5, 10);
INSERT INTO exercise_body_part VALUES (6, 11);
INSERT INTO exercise_body_part VALUES (7, 9);
INSERT INTO exercise_body_part VALUES (8, 12);

-- Push (Chest=2, Triceps=3, Shoulders=4, Deltoids=5)
INSERT INTO exercise_body_part VALUES (9, 2);
INSERT INTO exercise_body_part VALUES (9, 3);
INSERT INTO exercise_body_part VALUES (9, 4);

INSERT INTO exercise_body_part VALUES (10, 2);
INSERT INTO exercise_body_part VALUES (11, 3);
INSERT INTO exercise_body_part VALUES (12, 3);
INSERT INTO exercise_body_part VALUES (13, 5);
INSERT INTO exercise_body_part VALUES (14, 4);
INSERT INTO exercise_body_part VALUES (15, 5);

-- Pull (Back=6, Biceps=7, Forearms=8)
-- Push-ups also work Chest, Triceps, Shoulders
INSERT INTO exercise_body_part VALUES (16, 2);
INSERT INTO exercise_body_part VALUES (16, 3);
INSERT INTO exercise_body_part VALUES (16, 4);

INSERT INTO exercise_body_part VALUES (17, 6);
INSERT INTO exercise_body_part VALUES (17, 7);

INSERT INTO exercise_body_part VALUES (18, 6);

INSERT INTO exercise_body_part VALUES (19, 7);
INSERT INTO exercise_body_part VALUES (20, 7);
INSERT INTO exercise_body_part VALUES (21, 8);
INSERT INTO exercise_body_part VALUES (22, 7);
INSERT INTO exercise_body_part VALUES (23, 8);
INSERT INTO exercise_body_part VALUES (24, 8);


