USE db;

CREATE TABLE tb_order (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

INSERT INTO tb_order (name) VALUES ('original');
