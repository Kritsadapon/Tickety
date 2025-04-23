-- MySQL dump 10.13  Distrib 5.7.24, for osx11.1 (x86_64)
--
-- Host: 127.0.0.1    Database: login_webapp
-- ------------------------------------------------------
-- Server version	5.5.5-10.11.11-MariaDB-ubu2204

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `tbl_user`
--

DROP TABLE IF EXISTS `tbl_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tbl_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `password` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  `username` varchar(255) NOT NULL,
  `displayName` varchar(255) DEFAULT NULL,
  `display_name` varchar(255) DEFAULT NULL,
  `profile_picture` blob DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKk0bty7tbcye41jpxam88q5kj2` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbl_user`
--

LOCK TABLES `tbl_user` WRITE;
/*!40000 ALTER TABLE `tbl_user` DISABLE KEYS */;
INSERT INTO `tbl_user` VALUES (1,'$2a$10$2Bn8WTMBb3KM9g7TZ8pVduInyBA/eT6o9azBaS1GhHR.KbImkx.CS','USER','admin',NULL,NULL),(2,'$2a$10$1R37F5l88a/haPmEBCjg9uYesFr0t1DVIdnLqvTLHP3LPVUKJzhbO','USER','newUser',NULL,'New User'),(3,'$2a$10$A7.ZgyR87.Sr/dgCws1zBef7Twc7lnqBCRLERuo9bNN/QzeWVXr7K','USER','newUser1',NULL,'New User'),(4,'$2a$10$2lxUcne1dpAxApJZmccdmuYZ35GfTgTf2zNPXSM0l9AUxO.kady2a','USER','9sdy1',NULL,'9Sdy1'),(5,'$2a$10$VtoHBOsy0B9ImETQAzVSmeCKC9regFDq/3.Uf0gXg9AlixArpe4c.','USER','testuser_1',NULL,'TESTUSER1'),(6,'$2a$10$nwmCCs344I3vAhnYdbGhauyqYjB7kuD1ff.E6l2iQkUHMjaS9JyfO','USER','testuser_2',NULL,NULL),(7,'$2a$10$zNip5u9AeVa7dnvruGJb4ex9Z1QjXapTw.Gx0ASoWfpj7Ja7uzJdG','USER','9syd1',NULL,NULL),(8,'$2a$10$QMM7NjEu0HZ3cnoQ4vxsYOyKKzbB7Zs4lj59WKcs7sivGTvWVZhta','USER','testuser_3',NULL,'TESTUSER3'),(9,'$2a$10$IlnHC/i7hsKzibF4AIsXTe/QTr2wKM5qAXeTj6cOmb5FtHtOUByVi','USER','testuser_4',NULL,'TESTUSER4'),(10,'$2a$10$wg3nCRvdGcHO4Mec9YI01euBxVu46/pAsQ0ZeDQlBhH0WRAzF9LNi','USER','testuser_5',NULL,'TESTUSER5');
/*!40000 ALTER TABLE `tbl_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbl_forum`
--

DROP TABLE IF EXISTS `tbl_forum`;
CREATE TABLE `tbl_forum` (
    `forumid` bigint(20) PRIMARY KEY AUTO_INCREMENT,
    `user_id` bigint(20) NOT NULL,
    `forum_title` VARCHAR(256) NOT NULL,
    `forum_description` TEXT NOT NULL,
    `forum_date_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `tbl_user`(`id`) ON DELETE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dummy data for tbl_forum
INSERT INTO `tbl_forum` (`forumid`, `user_id`, `forum_title`, `forum_description`, `forum_date_time`) VALUES
(1, 1, 'General Discussion', 'A forum for general topics and discussions.', '2025-03-21 10:00:00');

--
-- Table structure for table `tbl_forum_follow`
--

CREATE TABLE `tbl_forum_follow` (
    `id` bigint(20) PRIMARY KEY AUTO_INCREMENT,
    `forum_id` bigint(20) NOT NULL,
    `user_id` bigint(20) NOT NULL,
    UNIQUE KEY `unique_forum_user` (`forum_id`, `user_id`),
    FOREIGN KEY (`forum_id`) REFERENCES `tbl_forum`(`forumid`) ON DELETE CASCADE,
    FOREIGN KEY (`user_id`) REFERENCES `tbl_user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dummy data for tbl_forum_follow

--
-- Table structure for table `tbl_forum`
--

DROP TABLE IF EXISTS `tbl_post`;
CREATE TABLE `tbl_post` (
    `postid` bigint(20) PRIMARY KEY AUTO_INCREMENT,
    `user_id` bigint(20) NOT NULL,
    `forumid` bigint(20) NOT NULL,
    `post_title` VARCHAR(256) NOT NULL,
    `post_description` TEXT NOT NULL,
    `post_date_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `tbl_user`(`id`)  ON DELETE CASCADE,
    FOREIGN KEY (`forumid`) REFERENCES `tbl_forum`(`forumid`) ON DELETE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dummy data for tbl_post
INSERT INTO `tbl_post` (`postid`, `user_id`, `forumid`, `post_title`, `post_description`, `post_date_time`, `likes`) VALUES
(1, 1, 1, 'Welcome to the Forum', 'This is the first post in the General Discussion forum.', '2025-03-21 10:30:00', 5);

--
-- Table structure for table `tbl_post_interaction`
--

CREATE TABLE `tbl_post_interaction` (
    `id` BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT(20) NOT NULL,
    `post_id` BIGINT(20) NOT NULL,
    `liked` BOOLEAN NOT NULL,
    UNIQUE KEY `unique_user_post` (`user_id`, `post_id`),
    FOREIGN KEY (`user_id`) REFERENCES `tbl_user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`post_id`) REFERENCES `tbl_post`(`postid`) ON DELETE CASCADE
);

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `tbl_comment`;
CREATE TABLE `tbl_comment` (
  `comment_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `postid` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `comment` text NOT NULL,
  `date_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`comment_id`),
  FOREIGN KEY (`postid`) REFERENCES `tbl_post`(`postid`),
  FOREIGN KEY (`user_id`) REFERENCES `tbl_user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dummy data for tbl_comment
INSERT INTO `tbl_comment` (`comment_id`, `postid`, `user_id`, `comment`, `date_time`) VALUES
(1, 1, 1, 'First!', '2025-03-21 10:45:00');

--
-- Table structure for table `tbl_team_invitation`
--

DROP TABLE IF EXISTS `tbl_team_invitation`;
CREATE TABLE `tbl_team_invitation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `team_id` bigint(20) NOT NULL,
  `invited_user_id` bigint(20) NOT NULL,
  `inviter_id` bigint(20) NOT NULL,
  `invitation_date` datetime NOT NULL,
  `accepted` tinyint(1) NOT NULL DEFAULT 0,
  `rejected` tinyint(1) NOT NULL DEFAULT 0,
  `response_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `team_id` (`team_id`),
  KEY `invited_user_id` (`invited_user_id`),
  KEY `inviter_id` (`inviter_id`),
  CONSTRAINT `fk_team_invitation_team` FOREIGN KEY (`team_id`) REFERENCES `tbl_team` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_team_invitation_invited_user` FOREIGN KEY (`invited_user_id`) REFERENCES `tbl_user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_team_invitation_inviter` FOREIGN KEY (`inviter_id`) REFERENCES `tbl_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Restore previous settings
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
