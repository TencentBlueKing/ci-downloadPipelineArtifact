package com.tencent.bk.devops.atom.task;

data class BuildHistory(
    val id: String,
    val userId: String,
    val trigger: String,
    val buildNum: Int?,
    val pipelineVersion: Int,
    val startTime: Long,
    val endTime: Long?,
    val status: String,
    val deleteReason: String?,
    val currentTimestamp: Long,
    val isMobileStart: Boolean = false,
    val material: List<PipelineBuildMaterial>?,
    val queueTime: Long?,
    val artifactList: List<FileInfo>?,
    val remark: String?,
    val totalTime: Long?,
    val executeTime: Long?,
    val buildParameters: List<BuildParameters>?,
    val webHookType: String?,
    val startType: String?,
    val recommendVersion: String?,
    val retry: Boolean = false
)