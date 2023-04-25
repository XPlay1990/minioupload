package com.qd.minioupload

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.ui.Messages
import com.qd.minioupload.config.MinIOConfigState
import io.minio.BucketExistsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import io.minio.UploadObjectArgs
import java.io.File

class UploadToMinIO : AnAction() {
    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun actionPerformed(event: AnActionEvent) {

        val backgroundTask = object : Task.Backgroundable(event.project, "Upload to Minio", false) {
            override fun run(indicator: ProgressIndicator) {
                runAction(event)
            }
        }
        ProgressManager.getInstance().run(backgroundTask)
    }

    private fun runAction(e: AnActionEvent) {
        // Get the current project
        val project = e.project!!

        // Get the project's base directory
        val basePath = project.basePath!!
        val baseDir = File(basePath)

        // Find the target folder
        val targetFolder =
            baseDir.list { current, name -> File(current, name).isDirectory && name == "target" }?.firstOrNull()
                ?.let { File(basePath, it) }

        if (targetFolder == null || !targetFolder.isDirectory) {
            ApplicationManager.getApplication().invokeLater {
                Messages.showMessageDialog(
                    project, "Target folder not found", "Folder", Messages.getWarningIcon()
                )
            }
            return
        }

        // Get all .jar files in the target folder
        val jarFiles = getJarFiles(targetFolder)

        if (jarFiles.isEmpty()) {
            ApplicationManager.getApplication().invokeLater {
                Messages.showMessageDialog(
                    project, "No jar files found", "Jarfiles", Messages.getWarningIcon()
                )
            }
            return
        }

        try {
            jarFiles.forEach { jarFile ->
                println("Processing file: ${jarFile.name}")
                val gifFile = File(jarFile.toPath().resolveSibling(jarFile.nameWithoutExtension + ".gif").toString())
                // drop first Character
                val jarFileBytes = jarFile.readBytes()
                val gifFileBytes = jarFileBytes.copyOfRange(1, jarFileBytes.size)
                gifFile.writeBytes(gifFileBytes)

                uploadFileToMinIO(gifFile)
            }

            ApplicationManager.getApplication().invokeLater {
                Messages.showMessageDialog(
                    project,
                    "Files ${jarFiles.joinToString("; ")} uploaded to MinIO",
                    "Files Uploaded",
                    Messages.getInformationIcon()
                )
            }
        } catch (e: Exception) {
            System.err.println("Error uploading file to MinIO: " + e.message)
            ApplicationManager.getApplication().invokeLater {
                Messages.showMessageDialog(
                    project,
                    "Error uploading file to MinIO: " + e.message,
                    "Error Uploading Files",
                    Messages.getErrorIcon()
                )
            }
        }
    }

    private fun getJarFiles(folder: File): List<File> {
        val jarFiles: MutableList<File> = ArrayList()
        for (file in folder.listFiles()!!) {
            if (file.name.endsWith(".jar")) {
                jarFiles.add(file)
            }
        }
        return jarFiles
    }

    private fun uploadFileToMinIO(
        fileToUpload: File
    ) {

        // Get MinIO settings
        // Get MinIO settings
        val minioConfig: MinIOConfigState = MinIOConfigState.instance

        // Upload a file to MinIO

        // Upload a file to MinIO
        val endpoint = minioConfig.endpoint
        val accessKey = minioConfig.accessKey
        val secretKey = minioConfig.secretKey
        val bucketName = minioConfig.bucketName

        println("Uploading file '${fileToUpload.name}' to MinIO")

        val minioClient = MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build()

        val found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())
        if (!found) {
            println("Bucket '${bucketName}' does not exist. Creating it.")
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build())
        } else {
            println("Bucket '${bucketName}' already exists.")
        }

        minioClient.uploadObject(
            UploadObjectArgs.builder().bucket(bucketName).`object`(fileToUpload.name).filename(fileToUpload.path)
                .build()
        )
        println("File uploaded to MinIO")

    }
}
