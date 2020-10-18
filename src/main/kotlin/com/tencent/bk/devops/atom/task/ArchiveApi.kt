package com.tencent.bk.devops.atom.task

import com.fasterxml.jackson.module.kotlin.readValue
import com.tencent.bk.devops.atom.api.BaseApi
import com.tencent.bk.devops.atom.pojo.Result
import com.tencent.bk.devops.atom.task.JsonUtils.objectMapper
import com.tencent.bk.devops.atom.utils.json.JsonUtil
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.util.HashSet

class ArchiveApi : BaseApi() {
    private val atomHttpClient = AtomHttpClient()

    fun downloadPipelineFile(user: String, projectId: String, pipelineId: String, buildId: String, uri: String, destPath: File, size: Long, bkWorkSpace: String) {
        logger.info("downloadPipelineFile: user: $user, projectId: $projectId, pipelineId: $pipelineId, buildId: $buildId, uri: $uri, destPath: $destPath")
        val url = "/bkrepo/api/build/generic/$projectId/pipeline$uri"
        var header = HashMap<String, String>();
        header.set("X-BKREPO-UID", user)
        val request = buildGet(url, header)
        downloadFile(request, destPath)
    }

    private fun downloadFile(request: Request, destPath: File) {
        return
    }

    fun matchFile(
        user: String,
        projectId: String,
        repoName: String,
        srcPath: String,
        pipelineId: String = "",
        buildId: String = "",
        logger: Logger
    ): List<FileInfo> {
        val result = mutableListOf<FileInfo>()

        return result
    }

    fun downloadFile(
        user: String,
        targetProjectId: String,
        targetPipelineId: String,
        buildHistory: BuildHistory,
        srcPaths: String,
        dest: String,
        workspace: String
    ): Int {
        var dest: String? = dest
        val targetPipelineName = getPipelineName(targetProjectId, targetPipelineId)
        val buildId = buildHistory.id

        var downloadCount = 0
        try {
            if (StringUtils.isBlank(dest)) {
                dest = "."
            }
            val destPath = File(File(workspace), dest)
            val paths = srcPaths.split(",").toTypedArray()
            for (patters in paths) {
                val path = patters.removePrefix("./").removePrefix("/")
                var pathDownloadCount = 0
                var jfrogFiles = matchFile(user, targetProjectId, "pipeline", path, targetPipelineId, buildId, logger)
                logger.info(jfrogFiles.size.toString() + " file match, target path: " + workspace)
                for (jfrogFile in jfrogFiles) {
                    logger.info("start download file: " + jfrogFile.path)
                    val destFile = File(destPath, jfrogFile.name)
                    logger.info("destFile: $destFile")
                    downloadPipelineFile(
                        user,
                        targetProjectId,
                        targetPipelineId,
                        buildId,
                        jfrogFile.path,
                        destFile,
                        jfrogFile.size,
                        workspace
                    )
                    logger.info("download file " + jfrogFile.path + " done")
                    pathDownloadCount++
                }

                if (pathDownloadCount == 0) {
                    logger.info("no file match for path: $path")
                } else {
                    downloadCount += pathDownloadCount
                }
            }
        } catch (e: java.lang.Exception) {
            logger.error("download faile error: " + e.message)
        }
        return downloadCount
    }

    fun getLatestSuccessBuild(projectId: String, pipelineId: String): BuildHistory? {
        val url = String.format("/process/api/build/builds/%s/%s/latestSuccessBuild", projectId, pipelineId)
        val request = atomHttpClient.buildAtomGet(url)
        val responseContent = atomHttpClient.doRequestWithContent(request)
        val result: Result<BuildHistory> = objectMapper.readValue(responseContent)
        return result.data
    }

    fun getPipelineName(projectId: String, pipelineId: String): String? {
        val url = String.format("/process/api/build/pipelines/%s/getPipelineNames", projectId)
        val pipelineIds: MutableSet<String> = HashSet()
        pipelineIds.add(pipelineId)
        val request = atomHttpClient.buildAtomPost(url, RequestBody.create(MediaType.parse("application/json; charset=utf-8"), JsonUtil.toJson<Set<String>>(pipelineIds)))
        val responseContent = atomHttpClient.doRequestWithContent(request!!)
        val result: Result<Map<String, String>> = objectMapper.readValue(responseContent)
        return result.data[pipelineId]
    }

    fun getSingleBuildHistory(projectId: String, pipelineId: String, buildNum: String): BuildHistory? {
        val url = String.format("/process/api/build/builds/%s/%s/%s/history?channelCode=BS", projectId, pipelineId, buildNum)
        val request = atomHttpClient.buildAtomGet(url)
        val responseContent = atomHttpClient.doRequestWithContent(request)
        val result: Result<BuildHistory> = objectMapper.readValue(responseContent)
        return result.data
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ArchiveApi::class.java)
    }
}