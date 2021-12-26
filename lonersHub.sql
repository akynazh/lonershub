CREATE DATABASE lonersHub;
USE lonersHub;
CREATE TABLE t_loner (
                         lonerId INT PRIMARY KEY AUTO_INCREMENT,
                         lonerName VARCHAR(255),
                         lonerPassword VARCHAR(32),
                         lonerEmail VARCHAR(127),
                         lonerResidence VARCHAR(32),
                         lonerAvatar BLOB
);
CREATE TABLE t_diary (
                         creatorId INT,
                         diaryId INT PRIMARY KEY AUTO_INCREMENT,
                         createDate DATE,
                         content TEXT,
                         FOREIGN KEY (creatorId) REFERENCES t_loner (lonerId)
);
CREATE TABLE t_video (
                         startTime DATETIME,
                         publisherId INT,
                         videoId INT PRIMARY KEY AUTO_INCREMENT,
                         content LONGBLOB,
                         description TEXT,
                         FOREIGN KEY (publisherId) REFERENCES t_loner(lonerId)
);
CREATE TABLE t_participant (
                               videoId INT PRIMARY KEY AUTO_INCREMENT,
                               participantId INT,
                               FOREIGN KEY (participantId) REFERENCES t_loner(lonerId),
                               FOREIGN KEY (videoId) REFERENCES t_video(videoId)
);
CREATE TABLE t_message (
                           messageId INT PRIMARY KEY AUTO_INCREMENT,
                           creatorId INT,
                           content TEXT,
                           FOREIGN KEY (creatorId) REFERENCES t_loner(lonerId)
);
INSERT INTO t_loner(lonerName, lonerPassword, lonerEmail, lonerResidence, lonerAvatar) VALUES ("admin", "658766", "1945561232@qq.com", "China Xian", null);
INSERT INTO t_diary(creatorId, createDate, content) VALUES (1, "2021-12-26", "first blood");
INSERT INTO t_video(startTime, publisherId, content, description) VALUES ("	2021-12-26 15:08:00", 1, "video1", "watch video1");
INSERT INTO t_participant(videoId, participantId) VALUES (1, 1);
INSERT INTO t_message(creatorId, content) VALUES (1, "hello");
