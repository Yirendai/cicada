use infra_cicada_web;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `trace_statis_info` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
