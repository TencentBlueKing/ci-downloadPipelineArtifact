package com.tencent.bk.devops.atom.task;

data class PipelineBuildMaterial(
    val aliasName: String?,
    val url: String,
    val branchName: String?,
    val newCommitId: String?,
    val newCommitComment: String?,
    val commitTimes: Int?
)