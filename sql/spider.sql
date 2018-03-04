CREATE DATABASE IF NOT EXISTS spider;

USE spider;

###课程表
DROP TABLE IF EXISTS spider.course;
CREATE TABLE spider.course (
  id             BIGINT PRIMARY KEY AUTO_INCREMENT,
  board_id       INT,
  title          VARCHAR(50),
  cware_name     VARCHAR(50),
  teacher_name   VARCHAR(30),
  year           VARCHAR(10),
  cware_class_id INT,
  open           INT,
  cware_id       INT NOT NULL UNIQUE,
  cware_title    VARCHAR(50),
  create_time    DATETIME           DEFAULT CURRENT_TIMESTAMP
);

###讲义表
DROP TABLE IF EXISTS spider.chapter;
CREATE TABLE spider.chapter (
  id            BIGINT   AUTO_INCREMENT, ##主键
  chapter_title VARCHAR(100), ##章节标题
  chapter_code  VARCHAR(10) NOT NULL UNIQUE, ##章节编码
  content       LONGTEXT, ##章节内容
  cware_id      INT, ##课程id
  course_title  VARCHAR(50), ##课程标题
  create_time   DATETIME DEFAULT CURRENT_TIMESTAMP, ##创建时间
  PRIMARY KEY (id, chapter_code)
)