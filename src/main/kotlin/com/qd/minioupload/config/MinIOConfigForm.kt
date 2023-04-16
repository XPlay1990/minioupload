package com.qd.minioupload.config

import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JPanel
import javax.swing.JPasswordField
import javax.swing.JTextField

class MinIOConfigForm {
    val mainPanel: JPanel
    val endpointTextField: JTextField = JBTextField()
    val bucketNameTextField: JTextField = JBTextField()
    val accessKeyTextField: JTextField = JBTextField()
    val secretKeyPasswordField: JPasswordField = JBPasswordField()

    init {
        mainPanel =
            FormBuilder.createFormBuilder().addLabeledComponent(JBLabel("Endpoint: "), endpointTextField, 1, true)
                .addLabeledComponent(JBLabel("Bucket: "), bucketNameTextField, 1, true)
                .addLabeledComponent(JBLabel("AccessKey: "), accessKeyTextField, 1, true)
                .addLabeledComponent(JBLabel("SecretKey: "), secretKeyPasswordField, 1, true).panel
    }
}
