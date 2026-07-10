-- AI 模块表结构（从 starpivot-cloud 迁移）
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ai_chat_message
-- ----------------------------
DROP TABLE IF EXISTS `ai_chat_message`;
CREATE TABLE `ai_chat_message`  (
  `message_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `conversation_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '会话标识',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'USER / ASSISTANT',
  `content` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
  `sort_order` int(0) NOT NULL DEFAULT 0 COMMENT '排序序号',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`message_id`) USING BTREE,
  INDEX `idx_ai_msg_conversation_sort`(`conversation_id`, `sort_order`) USING BTREE,
  INDEX `idx_ai_msg_conversation_time`(`conversation_id`, `create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'AI 对话消息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ai_chat_session
-- ----------------------------
DROP TABLE IF EXISTS `ai_chat_session`;
CREATE TABLE `ai_chat_session`  (
  `session_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `conversation_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '会话标识 user-{userId}:{uuid}',
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '新对话' COMMENT '会话标题',
  `message_count` int(0) NOT NULL DEFAULT 0 COMMENT '消息条数',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 1删除）',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`session_id`) USING BTREE,
  UNIQUE INDEX `uk_ai_chat_conversation`(`conversation_id`) USING BTREE,
  INDEX `idx_ai_chat_user_update`(`user_id`, `update_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'AI 对话会话' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ai_config
-- ----------------------------
DROP TABLE IF EXISTS `ai_config`;
CREATE TABLE `ai_config`  (
  `config_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置名称',
  `bot_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'AI 助手' COMMENT '助手名称',
  `bot_avatar` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '助手头像URL',
  `welcome_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '欢迎语模板，支持 {botName}',
  `system_prompt` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '系统提示词',
  `default_model` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'deepseek-chat' COMMENT '默认模型',
  `default_temperature` decimal(3, 2) NULL DEFAULT 0.70 COMMENT '默认温度',
  `max_memory_messages` int(0) NULL DEFAULT 30 COMMENT '会话记忆条数',
  `models_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '可选模型 JSON [{id,label}]',
  `rag_enabled` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '是否启用RAG（0是 1否）',
  `rag_top_k` int(0) NOT NULL DEFAULT 5 COMMENT 'RAG检索条数',
  `is_default` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '是否默认（0是 1否）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`config_id`) USING BTREE,
  UNIQUE INDEX `uk_ai_config_name`(`config_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'AI 基础配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ai_index_task
-- ----------------------------
DROP TABLE IF EXISTS `ai_index_task`;
CREATE TABLE `ai_index_task`  (
  `task_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `doc_id` bigint(0) NOT NULL COMMENT '文档ID',
  `task_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'INDEX_TEXT' COMMENT 'INDEX_TEXT / INDEX_FILE',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/RUNNING/DONE/FAILED',
  `retry_count` int(0) NOT NULL DEFAULT 0 COMMENT '重试次数',
  `error_msg` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '错误信息',
  `started_at` datetime(0) NULL DEFAULT NULL COMMENT '开始时间',
  `finished_at` datetime(0) NULL DEFAULT NULL COMMENT '完成时间',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`task_id`) USING BTREE,
  INDEX `idx_ai_index_task_doc`(`doc_id`, `status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'AI 知识库索引任务' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ai_knowledge_base
-- ----------------------------
DROP TABLE IF EXISTS `ai_knowledge_base`;
CREATE TABLE `ai_knowledge_base`  (
  `kb_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '知识库ID',
  `kb_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '知识库名称',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
  `top_k` int(0) NOT NULL DEFAULT 5 COMMENT '检索返回条数',
  `chunk_size` int(0) NOT NULL DEFAULT 600 COMMENT '分块大小（字符）',
  `chunk_overlap` int(0) NOT NULL DEFAULT 80 COMMENT '分块重叠（字符）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 1删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`kb_id`) USING BTREE,
  INDEX `idx_ai_kb_status`(`status`, `del_flag`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'AI 知识库' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ai_knowledge_chunk
-- ----------------------------
DROP TABLE IF EXISTS `ai_knowledge_chunk`;
CREATE TABLE `ai_knowledge_chunk`  (
  `chunk_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '分块ID',
  `doc_id` bigint(0) NOT NULL COMMENT '文档ID',
  `kb_id` bigint(0) NOT NULL COMMENT '知识库ID',
  `chunk_index` int(0) NOT NULL DEFAULT 0 COMMENT '分块序号',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分块内容',
  `embedding_json` json NULL COMMENT '向量嵌入 JSON 数组',
  `page_num` int(0) NULL DEFAULT NULL COMMENT '来源页码/节序号',
  `section_title` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '章节标题',
  `doc_version` int(0) NOT NULL DEFAULT 1 COMMENT '文档版本号',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`chunk_id`) USING BTREE,
  INDEX `idx_ai_chunk_doc`(`doc_id`, `chunk_index`) USING BTREE,
  INDEX `idx_ai_chunk_kb`(`kb_id`) USING BTREE,
  FULLTEXT INDEX `ft_ai_chunk_content`(`content`) WITH PARSER `ngram`
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'AI 知识库分块' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ai_knowledge_document
-- ----------------------------
DROP TABLE IF EXISTS `ai_knowledge_document`;
CREATE TABLE `ai_knowledge_document`  (
  `doc_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '文档ID',
  `kb_id` bigint(0) NOT NULL COMMENT '知识库ID',
  `title` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文档标题',
  `content` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文档正文（文本或解析后全文）',
  `source_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'TEXT' COMMENT '来源类型 TEXT/FILE',
  `original_file_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '原始文件名',
  `file_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件类型 PDF/DOCX/MD/TXT',
  `file_size` bigint(0) NULL DEFAULT NULL COMMENT '文件大小字节',
  `object_name` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'OSS对象路径',
  `chunk_count` int(0) NOT NULL DEFAULT 0 COMMENT '分块数量',
  `index_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'DONE' COMMENT '索引状态 PENDING/PROCESSING/DONE/FAILED',
  `error_msg` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '索引错误信息',
  `doc_version` int(0) NOT NULL DEFAULT 1 COMMENT '文档版本号',
  `indexed_at` datetime(0) NULL DEFAULT NULL COMMENT '最近索引完成时间',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`doc_id`) USING BTREE,
  INDEX `idx_ai_doc_kb`(`kb_id`, `status`, `del_flag`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'AI 知识库文档' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ai_usage_log
-- ----------------------------
DROP TABLE IF EXISTS `ai_usage_log`;
CREATE TABLE `ai_usage_log`  (
  `log_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint(0) NULL DEFAULT NULL COMMENT '用户ID',
  `conversation_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会话ID',
  `model` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '模型',
  `request_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'SEND / STREAM',
  `prompt_tokens` int(0) NOT NULL DEFAULT 0 COMMENT '输入 tokens',
  `completion_tokens` int(0) NOT NULL DEFAULT 0 COMMENT '输出 tokens',
  `total_tokens` int(0) NOT NULL DEFAULT 0 COMMENT '总 tokens',
  `latency_ms` bigint(0) NOT NULL DEFAULT 0 COMMENT '耗时毫秒',
  `success` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '是否成功（0是 1否）',
  `error_message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '错误信息',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`log_id`) USING BTREE,
  INDEX `idx_ai_usage_user_time`(`user_id`, `create_time`) USING BTREE,
  INDEX `idx_ai_usage_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'AI 调用用量日志' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

