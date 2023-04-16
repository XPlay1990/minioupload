package com.qd.minioupload.config

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil


@State(name = "MinIOConfig", storages = [Storage("minioConfig.xml")])
class MinIOConfigState : PersistentStateComponent<MinIOConfigState?> {
    var endpoint = ""
    var bucketName = ""
    var accessKey = ""
    var secretKey = ""
    override fun getState(): MinIOConfigState {
        return this
    }

    override fun loadState(state: MinIOConfigState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        val instance: MinIOConfigState
            get() = ApplicationManager.getApplication().getService(MinIOConfigState::class.java)
    }
}

