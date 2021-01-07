package com.example.emergencybackupv10

import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class Compressor {
    private var byteOutReadStream = ByteArrayOutputStream()
    private lateinit var zipOutStream: ZipOutputStream

    fun newFile(fileName: String): ByteArray{
        zipOutStream = ZipOutputStream(byteOutReadStream)
        val zipEntry = ZipEntry(fileName)
        zipOutStream.putNextEntry(zipEntry)
        return byteOutReadStream.toByteArray()
    }

    fun compressData(data: ByteArray): ByteArray{
        byteOutReadStream.reset()
        zipOutStream.write(data, 0, data.size)
        return byteOutReadStream.toByteArray()
    }

    fun positionNextEntry(fis: FileInputStream){

    }

    fun finalizeCompression(): ByteArray{
        byteOutReadStream.reset()
        zipOutStream.closeEntry()
        zipOutStream.finish()
        val entryClosing = byteOutReadStream.toByteArray()
        zipOutStream.close()
        return entryClosing
    }
}