package com.tencent.bk.devops.atom.task

import com.tencent.bk.devops.atom.pojo.AtomBaseParam
import lombok.Data
import lombok.EqualsAndHashCode

@Data
@EqualsAndHashCode(callSuper = true)
class DownloadPipelineArtifactParam : AtomBaseParam() {
    var srcPipelineId = ""
    var buildNo = ""
    var buildNoSelector = ""
    var srcPath = ""
    var destPath = ""
    var isContinue = ""
}