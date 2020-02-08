package org.giogt.web.server.it

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

object TestFiles {
    private val bufferSize = 1024 * 8 // 8K

    fun getResourceBytes(resourcePath: String): ByteArray {
        val stream = Thread.currentThread()
                .contextClassLoader
                .getResourceAsStream(resourcePath)

        val baos = ByteArrayOutputStream()
        val buff = ByteArray(bufferSize)
        var nBytesRead = stream.read(buff)
        while (nBytesRead != -1) {
            baos.write(buff, 0, nBytesRead)
            nBytesRead = stream.read(buff)
        }

        return baos.toByteArray()
    }

    fun createDirectories(dirPath: Path) = Files.createDirectories(dirPath)!!

    fun deleteDirectoryRecursively(dirPath: Path) = Files.walkFileTree(dirPath,
            object : SimpleFileVisitor<Path>() {
                override fun postVisitDirectory(
                        dir: Path, exc: IOException?): FileVisitResult {
                    Files.delete(dir)
                    return FileVisitResult.CONTINUE
                }

                override fun visitFile(
                        file: Path, attrs: BasicFileAttributes): FileVisitResult {
                    Files.delete(file)
                    return FileVisitResult.CONTINUE
                }
            }
    )!!
}
