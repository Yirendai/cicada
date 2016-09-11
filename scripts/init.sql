CREATE TABLE `app_info` (
      `id` int(11) NOT NULL AUTO_INCREMENT,
      `app_name` varchar(255) DEFAULT NULL,
      `register_time` datetime DEFAULT NULL,
      PRIMARY KEY (`id`),
      UNIQUE KEY `app_name` (`app_name`)
) ENGINE=MyISAM AUTO_INCREMENT=29 DEFAULT CHARSET=utf8;

CREATE TABLE `service_info` (
      `id` int(11) NOT NULL AUTO_INCREMENT,
      `app_id` int(11) DEFAULT NULL,
      `register_time` datetime DEFAULT NULL,
      `service_name` varchar(255) DEFAULT NULL,
      PRIMARY KEY (`id`),
      UNIQUE KEY `app_service` (`app_id`,`service_name`)
) ENGINE=MyISAM AUTO_INCREMENT=48 DEFAULT CHARSET=utf8;

CREATE TABLE `method_info` (
      `id` int(11) NOT NULL AUTO_INCREMENT,
      `method_name` varchar(255) DEFAULT NULL,
      `register_time` datetime DEFAULT NULL,
      `service_id` int(11) NOT NULL,
      PRIMARY KEY (`id`),
      UNIQUE KEY `service_method` (`service_id`,`method_name`)
) ENGINE=MyISAM AUTO_INCREMENT=56 DEFAULT CHARSET=utf8;

CREATE TABLE `span_statis_info` (
      `id` bigint(20) NOT NULL AUTO_INCREMENT,
      `app_id` int(11) NOT NULL,
      `avg_duration` double NOT NULL,
      `count` bigint(20) NOT NULL,
      `failure_rate` double NOT NULL,
      `line95duration` int(11) NOT NULL,
      `line999duration` int(11) NOT NULL,
      `max_duration` int(11) NOT NULL,
      `method_id` int(11) NOT NULL,
      `min_duration` int(11) NOT NULL,
      `service_id` int(11) NOT NULL,
      `statis_time` datetime DEFAULT NULL,
      PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=22646 DEFAULT CHARSET=utf8;
