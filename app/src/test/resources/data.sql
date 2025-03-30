DROP TABLE IF EXISTS tb_role CASCADE;
DROP TABLE IF EXISTS tb_address CASCADE;
DROP TABLE IF EXISTS tb_attribute CASCADE;
DROP TABLE IF EXISTS tb_user CASCADE;
DROP TABLE IF EXISTS tb_order CASCADE;

CREATE TABLE tb_role (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL
);

INSERT INTO tb_role(id, name) VALUES (1, 'role#1');
INSERT INTO tb_role(id, name) VALUES (2, 'role#2');

CREATE TABLE tb_user (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    role_id INT,
    FOREIGN KEY (role_id) REFERENCES tb_role(id)
);

CREATE TABLE tb_attribute (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    val VARCHAR(100) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES tb_user(id)
);

CREATE TABLE tb_address (
    user_id INT NOT NULL,
    street VARCHAR(100) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES tb_user(id)
);

