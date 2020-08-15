-- revision=2019052101, apply_dt=2020-06-13 22:38:09

-- ==================== Basement-4(B4/10#):基础 =======================

-- sys_schema_version DdlTable
CREATE TABLE `sys_schema_version` (
  `revision` bigint(20) NOT NULL COMMENT '版本号+修订号',
  `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建日时',
  `modify_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改日时',
  `commit_id` bigint(20) NOT NULL COMMENT '提交ID',
  `apply_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT '执行日时',
  `comments` varchar(500) NOT NULL DEFAULT '' COMMENT 'sql路径信息',
  `upto_sql` text NOT NULL COMMENT '升级脚本',
  `undo_sql` text NOT NULL COMMENT '降级脚本',
  PRIMARY KEY (`revision`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='101/表结构版本';

-- sys_schema_journal DdlTable
CREATE TABLE `sys_schema_journal` (
  `table_name` varchar(100) NOT NULL COMMENT '主表表名',
  `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建日时',
  `modify_dt` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改日时',
  `commit_id` bigint(20) NOT NULL COMMENT '提交ID',
  `ddl_updtbl` text NOT NULL COMMENT '更新的跟踪表DDL',
  `ddl_updtrg` text NOT NULL COMMENT '更新的触发器DDL',
  `ddl_deltbl` text NOT NULL COMMENT '删除的跟踪表DDL',
  `ddl_deltrg` text NOT NULL COMMENT '删除的触发器DDL',
  `log_update` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT '开始跟踪更新的日时',
  `log_delete` datetime(3) NOT NULL DEFAULT '1000-01-01 00:00:00.000' COMMENT '开始跟踪删除的日时',
  PRIMARY KEY (`table_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='102/数据触发器';

-- sys_light_sequence DdlTable
CREATE TABLE `sys_light_sequence` (
  `seq_name` varchar(100) NOT NULL COMMENT '序列名',
  `block_id` int(11) NOT NULL DEFAULT '0' COMMENT '分块序号',
  `next_val` bigint(20) NOT NULL DEFAULT '1' COMMENT '下一个序号',
  `step_val` int(11) NOT NULL DEFAULT '100' COMMENT '序列步长',
  `comments` varchar(200) NOT NULL COMMENT '注释说明',
  PRIMARY KEY (`seq_name`,`block_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='103/序号生成器';

-- sys_commit_journal DdlTable
CREATE TABLE `sys_commit_journal` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `create_dt` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建日时',
  `event_name` varchar(200) NOT NULL COMMENT '事件名称',
  `target_key` varchar(200) NOT NULL DEFAULT '' COMMENT '目标数据特征',
  `login_info` text COMMENT '登陆信息，用户，终端等',
  `other_info` text COMMENT '其他信息，业务侧自定义',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='104/数据变更集';

-- ==================== Basement-3(B3/15#):多语言，多时区，多货币 =======================

-- sys_constant_enum DdlTable
CREATE TABLE `sys_constant_enum` (
  `id` int(11) NOT NULL COMMENT 'id:动态9位数起，静态8位以下，建议3-2-2分段（表-段-值）,00结尾为SUPER',
  `type` varchar(100) NOT NULL COMMENT 'enum分组:相同type为同一Enum，自动Pascal命名',
  `code` varchar(100) NOT NULL COMMENT 'enum名字:为SUPER时固定code|id，表示对外key,编码友好',
  `desc` varchar(100) NOT NULL COMMENT '默认名字:线上',
  `info` varchar(500) NOT NULL COMMENT '扩展信息:分类，过滤等，如果SUPER时，为模板Resource格式',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='105/常量枚举:自动生成enum类';

-- sys_standard_i18n DdlTable
CREATE TABLE `sys_standard_i18n` (
  `base` varchar(100) NOT NULL COMMENT '基集合:表或类',
  `kind` varchar(100) NOT NULL COMMENT '多国语字段：code或列名',
  `ukey` varchar(200) NOT NULL COMMENT '唯一键:code|id###',
  `lang` char(5) NOT NULL COMMENT '国家语言标记，下划线分隔:zh_CN',
  `text` varchar(3000) NOT NULL COMMENT '语言内容:中国/东北三省|攻城狮',
  PRIMARY KEY (`base`,`kind`,`ukey`,`lang`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='106/标准多国语';

-- ==================== Floor-10(F11/90#):辅助 =======================

