package com.tencent.bk.devops.atom.task

import com.tencent.bk.devops.atom.AtomContext
import com.tencent.bk.devops.atom.exception.AtomException
import com.tencent.bk.devops.atom.pojo.StringData
import com.tencent.bk.devops.atom.spi.AtomService
import com.tencent.bk.devops.atom.spi.TaskAtom
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Paths
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@AtomService(paramClass = DownloadPipelineArtifactParam::class)
class DownloadPipelineArtifactAtom : TaskAtom<DownloadPipelineArtifactParam> {
    override fun execute(atomContext: AtomContext<DownloadPipelineArtifactParam>) {
        val atomParam = atomContext.param
        val atomResult = atomContext.result
        checkAndInitParam(atomParam)

        val targetPipelineId = atomParam.srcPipelineId
        val buildSelectType = atomParam.buildNo
        val targetBuildNo = atomParam.buildNoSelector
        val useLatestSuccessBuild: String = atomParam.isContinue
        val currentPipelineId = atomParam.pipelineId
        val currentBuildNum = atomParam.pipelineBuildNum
        val currentProjectId = atomParam.projectName
        val currentBuildId = atomParam.pipelineBuildId

        val srcPath = atomParam.srcPath
        val destPath = atomParam.destPath
        val workspace = atomParam.bkWorkspace
        val userId = atomParam.pipelineStartUserName
//        val isParallel = atomParam.isParallel == "true"

        var count = 0
        if ("false" == buildSelectType) { // 指定构建号
            val buildHistory: BuildHistory = archiveApi.getSingleBuildHistory(currentProjectId, targetPipelineId, targetBuildNo)
                ?: throw RuntimeException(String.format("build not found for buildNo: %s", targetBuildNo))
            count = archiveApi.downloadFile(userId, currentProjectId, targetPipelineId, buildHistory, srcPath, destPath, workspace)
        } else if ("true" == useLatestSuccessBuild) { // 最近成功构建号
            val buildHistory: BuildHistory = archiveApi.getLatestSuccessBuild(currentProjectId, targetPipelineId)
                ?: throw RuntimeException("no success build found")
            count = archiveApi.downloadFile(userId, currentProjectId, targetPipelineId, buildHistory, srcPath, destPath, workspace)
        } else { // 最新构建号
            var targetBuildNum: String? = "-1"
            if (currentPipelineId == targetPipelineId) { // 如果是当前流水线，最新构建号判定为本次执行
                targetBuildNum = currentBuildNum
            }
            val buildHistory: BuildHistory = archiveApi.getSingleBuildHistory(currentProjectId, targetPipelineId, targetBuildNo)
                ?: throw RuntimeException("build not found")
            count = archiveApi.downloadFile(userId, currentProjectId, targetPipelineId, buildHistory, srcPath, destPath, workspace)
        }
        if (count == 0) throw RuntimeException("no file downloaded!")
    }

    private fun checkAndInitParam(atomParam: DownloadPipelineArtifactParam) {
//        if (atomParam.reportName.isNullOrBlank()) {
//            throw AtomException("invalid reportName")
//        }
//        if (atomParam.indexFile.isNullOrBlank()) {
//            throw AtomException("invalid indexFile")
//        }
//        if (atomParam.fileDir.isNullOrBlank()) {
//            throw AtomException("invalid fileDir")
//        }
//
//        var indexFileCharset = atomParam.indexFileCharset
//        if (indexFileCharset.isNullOrBlank()) {
//            indexFileCharset = "UTF-8"
//        }
//        if (indexFileCharset == "default") {
//            indexFileCharset = Charset.defaultCharset().name()
//        }
//        if (!Charset.availableCharsets().containsKey(indexFileCharset)) {
//            throw RuntimeException("unsupported charset: $indexFileCharset")
//        }
//        atomParam.indexFileCharset = indexFileCharset
    }

    companion object {
        private val logger = LoggerFactory.getLogger(DownloadPipelineArtifactAtom::class.java)
        var archiveApi = ArchiveApi()
    }
}