CREATE TABLE IF NOT EXISTS `pubenvironment` (
  `id` INT NOT NULL,
  `nlpname` VARCHAR(45) NULL COMMENT '名称',
  `nationid` INT NULL COMMENT '外健国家政策ID',
  `ienvironid` INT NULL COMMENT '外健国际环境ID',
  `naturlenvid` INT NULL COMMENT '外健自然环境ID',
  `industryid` INT NULL COMMENT '外健行业情况外健ID',
  PRIMARY KEY (`id`))

CREATE TABLE IF NOT EXISTS `Industrysituation` (
  `id` INT NOT NULL COMMENT '行业情况ID',
  `nlptime` VARCHAR(45) NULL COMMENT 'NLP处理时间',
  `nlpcontent` LONGTEXT NULL COMMENT 'NLP处理后行业情况',
  `nlptype` VARCHAR(45) NULL COMMENT '行业情况类别(当前两类一类资源缺乏类、行业变革累)',
  PRIMARY KEY (`id`))

CREATE TABLE IF NOT EXISTS `Naturalenv` (
  `id` INT NOT NULL COMMENT '自然环境事件ID',
  `nlptime` VARCHAR(45) NULL COMMENT 'NLP处理时间',
  `nlpcontent` LONGTEXT NULL COMMENT 'NLP处理后自然环境事件内容',
  `nlptype` VARCHAR(45) NULL COMMENT '自然环境类型',
  PRIMARY KEY (`id`))

CREATE TABLE IF NOT EXISTS `Nationalpolicy` (
  `id` INT NOT NULL,
  `nlptime` VARCHAR(45) NULL COMMENT '处理时间',
  `nlpcontent` LONGTEXT NULL COMMENT 'NLP处理后内容(事件触发后结果内容)',
  `nlptype` VARCHAR(45) NULL COMMENT '类别',
  PRIMARY KEY (`id`))

CREATE TABLE IF NOT EXISTS `Ienvironment` (
  `id` INT NOT NULL,
  `nlptime` VARCHAR(45) NULL COMMENT '数据处理时间',
  `nlpcontent` LONGTEXT NULL COMMENT '国际环境NLP处理后内容',
  `nlptype` VARCHAR(45) NULL COMMENT '国际环境变化类型',
  PRIMARY KEY (`id`))

  CREATE TABLE `nlprule` (
  `id` int(11) DEFAULT NULL,
  `rulename` varchar(200) DEFAULT NULL,
  `ruleformula` varchar(800) DEFAULT NULL,
  `ruletype` varchar(45) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

