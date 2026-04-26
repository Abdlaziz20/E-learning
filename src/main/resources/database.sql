-- ================================================
-- E-LEARNING PLATFORM DATABASE
-- Run this in phpMyAdmin or MySQL command line
-- ================================================

CREATE DATABASE IF NOT EXISTS elearning_db;
USE elearning_db;

-- CATEGORIES TABLE
CREATE TABLE IF NOT EXISTS categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    icon VARCHAR(50) DEFAULT 'book',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- USERS TABLE (base for students and teachers)
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('STUDENT', 'TEACHER') NOT NULL,
    avatar_color VARCHAR(10) DEFAULT '#4A90E2',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- COURSES TABLE
CREATE TABLE IF NOT EXISTS courses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    teacher_id INT NOT NULL,
    category_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

-- QUIZZES TABLE
CREATE TABLE IF NOT EXISTS quizzes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    teacher_id INT NOT NULL,
    category_id INT NOT NULL,
    time_limit INT DEFAULT 30,  -- minutes
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

-- QUESTIONS TABLE
CREATE TABLE IF NOT EXISTS questions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    quiz_id INT NOT NULL,
    question_text TEXT NOT NULL,
    option_a VARCHAR(500) NOT NULL,
    option_b VARCHAR(500) NOT NULL,
    option_c VARCHAR(500) NOT NULL,
    option_d VARCHAR(500) NOT NULL,
    correct_option ENUM('A','B','C','D') NOT NULL,
    points INT DEFAULT 1,
    order_num INT DEFAULT 0,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE
);

-- RESULTS TABLE
CREATE TABLE IF NOT EXISTS results (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    quiz_id INT NOT NULL,
    score INT NOT NULL,
    total_questions INT NOT NULL,
    correct_answers INT NOT NULL,
    time_taken INT DEFAULT 0,  -- seconds
    taken_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE
);

-- ================================================
-- SEED DATA
-- ================================================

INSERT INTO categories (name, description, icon) VALUES
('Mathematics', 'Numbers, algebra, geometry and more', 'calculator'),
('Programming', 'Coding, algorithms, data structures', 'code'),
('Physics', 'Mechanics, electricity, quantum physics', 'atom'),
('History', 'World history, civilizations, events', 'globe'),
('English', 'Grammar, literature, writing skills', 'book'),
('Biology', 'Living organisms, cells, evolution', 'leaf'),
('Chemistry', 'Elements, compounds, reactions', 'flask'),
('Geography', 'Countries, maps, climate', 'map');

-- Sample Teacher
INSERT INTO users (full_name, email, password, role, avatar_color) VALUES
('Dr. Ahmed Ben Ali', 'teacher@demo.com', 'teacher123', 'TEACHER', '#7C3AED'),
('Prof. Sarah Johnson', 'sarah@demo.com', 'sarah123', 'TEACHER', '#059669');

-- Sample Student
INSERT INTO users (full_name, email, password, role, avatar_color) VALUES
('Mohamed Trabelsi', 'student@demo.com', 'student123', 'STUDENT', '#2563EB'),
('Fatima Zahra', 'fatima@demo.com', 'fatima123', 'STUDENT', '#DC2626');

-- Sample Quiz
INSERT INTO quizzes (title, description, teacher_id, category_id, time_limit) VALUES
('Python Basics Quiz', 'Test your Python fundamentals', 1, 2, 15),
('Algebra Fundamentals', 'Basic algebra and equations', 1, 1, 20);

-- Sample Questions for Quiz 1
INSERT INTO questions (quiz_id, question_text, option_a, option_b, option_c, option_d, correct_option, points, order_num) VALUES
(1, 'What is the output of print(type(42))?', '<class int>', '<class num>', '<type int>', 'integer', 'A', 1, 1),
(1, 'Which keyword defines a function in Python?', 'function', 'def', 'fun', 'define', 'B', 1, 2),
(1, 'What does len([1,2,3]) return?', '2', '3', '4', 'Error', 'B', 1, 3),
(1, 'Which of these is a Python list?', '{1,2,3}', '(1,2,3)', '[1,2,3]', '<1,2,3>', 'C', 1, 4);

-- Sample Questions for Quiz 2
INSERT INTO questions (quiz_id, question_text, option_a, option_b, option_c, option_d, correct_option, points, order_num) VALUES
(2, 'What is 2x + 5 = 15, solve for x?', '4', '5', '6', '7', 'B', 1, 1),
(2, 'Which is the quadratic formula?', 'x = -b/2a', 'x = (-b ± √(b²-4ac)) / 2a', 'x = b/a', 'x = (b ± a) / c', 'B', 1, 2),
(2, 'What is the slope of y = 3x + 2?', '2', '3', '5', '1', 'B', 1, 3);
