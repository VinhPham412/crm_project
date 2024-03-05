-- SQL CRM

CREATE database crm_app;
USE crm_app;

CREATE TABLE IF NOT EXISTS roles (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(100),
    PRIMARY KEY (id)
);

-- CREATE TABLE IF NOT EXISTS users (
--     id INT NOT NULL AUTO_INCREMENT,
--     email VARCHAR(100) NOT NULL,
--     password VARCHAR(100) NOT NULL,
--     fullname VARCHAR(100) NOT NULL,
--     avatar VARCHAR(100),
--     role_id INT NOT NULL,
--     PRIMARY KEY (id)
-- );

CREATE TABLE IF NOT EXISTS users (
    id INT NOT NULL AUTO_INCREMENT,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    fullname VARCHAR(100) NOT NULL,
    username VARCHAR(100) CHECK (username LIKE '@%') NOT NULL, -- username phải bắt đầu bằng @
    avatar VARCHAR(100),
    phone_no VARCHAR(11) CHECK (phone_no REGEXP '^[0-9]{10}$') NOT NULL, -- SĐT phải có 10 chữ số
    role_id INT NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS status (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS jobs (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(100),
    start_date DATE,
    end_date DATE,
    user_id INT NOT NULL, -- user_id là id của leader quản lý dự án
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS tasks (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(100),
    start_date DATE,
    end_date DATE,
    user_id INT NOT NULL, -- user_id là id của nhân viên thường phụ trách task này
    job_id INT NOT NULL, -- job_id là id của dự án mà task này thuộc về
    status_id INT NOT NULL, -- status_id là mã trạng thái
    PRIMARY KEY (id)
);

ALTER TABLE users ADD FOREIGN KEY (role_id) REFERENCES roles (id)  ON DELETE CASCADE;
ALTER TABLE tasks ADD FOREIGN KEY (user_id) REFERENCES users (id)  ON DELETE CASCADE;
ALTER TABLE tasks ADD FOREIGN KEY (job_id) REFERENCES jobs (id)  ON DELETE CASCADE;
ALTER TABLE tasks ADD FOREIGN KEY (status_id) REFERENCES status (id)  ON DELETE CASCADE;
ALTER TABLE jobs ADD FOREIGN KEY (user_id) REFERENCES users (id)  ON DELETE CASCADE;

INSERT INTO roles( name, description ) VALUES ("ROLE_ADMIN", "Quản trị hệ thống");
INSERT INTO roles( name, description ) VALUES ("ROLE_LEADER", "Quản lý dự án");
INSERT INTO roles( name, description ) VALUES ("ROLE_USER", "Nhân viên");

INSERT INTO status( name ) VALUES ("Chưa thực hiện");
INSERT INTO status( name ) VALUES ("Đang thực hiện");
INSERT INTO status( name ) VALUES ("Đã hoàn thành");

INSERT INTO users( email, password, fullname, username, phone_no, role_id ) VALUES ("nguyenvannam@gmail.com", "admin123", "Nguyen Van Nam", "@Nam", "0367409536", 1);
INSERT INTO users( email, password, fullname, username, phone_no, role_id ) VALUES ("tranvantuan@gmail.com", "tuan123", "Tran Van Tuan", "@Tuan", "0365499574", 2);
INSERT INTO users( email, password, fullname, username, phone_no, role_id ) VALUES ("buiminhhieu@gmail.com", "hieu123", "Bui Minh Hieu", "@Hieu", "0935897614", 2);
INSERT INTO users( email, password, fullname, username, phone_no, role_id ) VALUES ("phamthilan@gmail.com", "lan123", "Pham Thi Lan", "@Lan", "0987654321", 3);
INSERT INTO users( email, password, fullname, username, phone_no, role_id ) VALUES ("nguyenvanlong@gmail.com", "long123", "Nguyen Van Long", "@Long", "0976684321", 3);
INSERT INTO users( email, password, fullname, username, phone_no, role_id ) VALUES ("tranvannghia@gmail.com", "nghia123", "Tran Van Nghia", "@Nghia", "0357650321", 3);
INSERT INTO users( email, password, fullname, username, phone_no, role_id ) VALUES ("buingocanh@gmail.com", "anh123", "Bui Ngoc Anh", "@NgocAnh", "0347654321", 3);

INSERT INTO jobs ( name, description, start_date, end_date, user_id ) VALUES ("CRM", "Dự án quản lý công việc", '2023-01-01', '2024-01-01', 2);
INSERT INTO jobs ( name, description, start_date, end_date, user_id ) VALUES ("Students Management", "Dự án quản lý sinh viên", '2023-03-01', '2024-03-01', 3);

INSERT INTO tasks ( name, start_date, end_date, user_id, job_id, status_id )
VALUES ("Phân tích yêu cầu khách hàng", '2023-01-01', '2023-01-07', 4, 1, 3);
INSERT INTO tasks ( name, start_date, end_date, user_id, job_id, status_id )
VALUES ("Phân tích hệ thống", '2023-01-08', '2023-01-15', 5, 1, 2);
INSERT INTO tasks ( name, start_date, end_date, user_id, job_id, status_id )
VALUES ("Thiết kế CSDL", '2023-02-01', '2023-02-15', 4, 1, 1);
INSERT INTO tasks ( name, start_date, end_date, user_id, job_id, status_id )
VALUES ("Phân tích hệ thống", '2023-02-07', '2023-02-15', 6, 2, 3);
INSERT INTO tasks ( name, start_date, end_date, user_id, job_id, status_id )
VALUES ("Làm chức năng Login", '2023-02-10', '2023-02-15', 6, 2, 2);
INSERT INTO tasks ( name, start_date, end_date, user_id, job_id, status_id )
VALUES ("Thiết kế giao diện", '2023-02-15', '2023-03-01', 7, 2, 1);

-- Câu truy vấn cho chức năng login
SELECT *
FROM users u
WHERE u.email = 'email_nguoi_dung_nhap' AND u.password = 'password_ng_dung_nhap';

-- Câu truy vấn cho chức năng add role
INSERT INTO roles( name, description ) VALUES ("ROLE_NAME", "Mô tả");

-- Câu truy vấn cho chức năng lấy toàn bộ danh sách role
SELECT *
FROM roles r;

-- Câu truy vấn cho chức năng add user
INSERT INTO users( email, password, fullname, username, phone_no, role_id ) VALUES ("nguyenvana@gmail.com", "123456", "Nguyen Van A", "@Tom", "0367409536", 1);
