CREATE DATABASE lonersHub;
USE lonersHub;
DROP TABLE IF EXISTS t_loner;
CREATE TABLE t_loner
(
    lonerId        INT PRIMARY KEY AUTO_INCREMENT,
    lonerName      VARCHAR(255),
    lonerPassword  VARCHAR(32),
    lonerEmail     VARCHAR(127),
    lonerSignature VARCHAR(32),
    lonerAvatarUrl Text
);
DROP TABLE IF EXISTS t_diary;
CREATE TABLE t_diary
(
    creatorId  INT,
    diaryId    INT PRIMARY KEY AUTO_INCREMENT,
    createTime DATETIME,
    content    TEXT,
    FOREIGN KEY (creatorId) REFERENCES t_loner (lonerId)
);
DROP TABLE IF EXISTS t_video;
CREATE TABLE t_video
(
    startTime       DATETIME,
    publisherId     INT,
    videoId         INT PRIMARY KEY AUTO_INCREMENT,
    videoUrl        TEXT,
    videoName       varchar(255),
    description     TEXT,
    participantsNum INT,
    FOREIGN KEY (publisherId) REFERENCES t_loner (lonerId)
);
DROP TABLE IF EXISTS t_participant;
CREATE TABLE t_participant
(
    id            INT PRIMARY KEY AUTO_INCREMENT,
    videoId       INT,
    participantId INT,
    FOREIGN KEY (participantId) REFERENCES t_loner (lonerId),
    FOREIGN KEY (videoId) REFERENCES t_video (videoId)
);
DROP TABLE IF EXISTS t_message;
CREATE TABLE t_message
(
    messageId   INT PRIMARY KEY AUTO_INCREMENT,
    creatorId   INT,
    creatorName VARCHAR(32),
    createTime  VARCHAR(64),
    videoId     INT,
    content     TEXT,
    FOREIGN KEY (creatorId) REFERENCES t_loner (lonerId),
    FOREIGN KEY (videoId) REFERENCES t_video (videoId)
);

DROP TABLE IF EXISTS t_global;
CREATE TABLE t_global
(
    visitCount LONG,
    id INT PRIMARY KEY
);

ALTER DATABASE lonersHub DEFAULT CHARACTER SET utf8;
ALTER TABLE t_loner
    CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE t_diary
    CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE t_video
    CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE t_message
    CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE t_participant
    CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE t_global
    CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
INSERT INTO t_global(visitCount, id) VALUES (0, 1);