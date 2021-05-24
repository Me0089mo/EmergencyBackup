package com.example.emergencybackupv10

import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.util.zip.Deflater
import java.util.zip.DeflaterOutputStream
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

    fun compressData(data: ByteArray, numBytes: Int): ByteArray{
        byteOutReadStream.reset()
        zipOutStream.write(data, 0, numBytes)
        return byteOutReadStream.toByteArray()
    }

    fun finalizeCompression(): ByteArray{
        byteOutReadStream.reset()
        zipOutStream.finish()
        val entryClosing = byteOutReadStream.toByteArray()
        zipOutStream.close()
        return entryClosing
    }
}