package com.qd.minioupload.config

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

class MinIOConfigurable : Configurable {
    private var form: MinIOConfigForm? = null
    override fun getDisplayName(): @Nls(capitalization = Nls.Capitalization.Title) String {
        return "MinIO Settings"
    }

    override fun createComponent(): JComponent {
        form = MinIOConfigForm()
        return form!!.mainPanel
    }

    override fun isModified(): Boolean {
        val config: MinIOConfigState = MinIOConfigState.instance
        return form!!.endpointTextField.text != config.endpoint || form!!.bucketNameTextField.text != config.bucketName || form!!.accessKeyTextField.text != config.accessKey || String(
            form!!.secretKeyPasswordField.password
        ) != config.secretKey
    }

    @Throws(ConfigurationException::class)
    override fun apply() {
        val config: MinIOConfigState = MinIOConfigState.instance
        config.endpoint = form!!.endpointTextField.text
        config.bucketName = form!!.bucketNameTextField.text
        config.accessKey = form!!.accessKeyTextField.text
        config.secretKey = String(form!!.secretKeyPasswordField.password)
    }

    override fun reset() {
        val config: MinIOConfigState = MinIOConfigState.instance
        form!!.endpointTextField.text = config.endpoint
        form!!.bucketNameTextField.text = config.bucketName
        form!!.accessKeyTextField.text = config.accessKey
        form!!.secretKeyPasswordField.text = config.secretKey
    }

    override fun disposeUIResources() {
        form = null
    }
}
