package com.example.emergencybackupv10

import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.*

class Compressor {
    private var byteOutReadStream = ByteArrayOutputStream()
    private lateinit var zipOutStream: ZipOutputStream
    private lateinit var zipInputStream: ZipInputStream

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
        byteOutReadStream.reset()
        return entryClosing
    }

    fun decompressFile(decipheredFile: File, fileOutStream: FileOutputStream){
        zipInputStream = ZipInputStream(FileInputStream(decipheredFile))
        var readingArray = ByteArray(1024)
        var reading = 0
        if(zipInputStream.nextEntry != null){
            while(reading != -1){
                reading = zipInputStream.read(readingArray)
                if(reading != -1)
                    fileOutStream.write(readingArray, 0, reading)
            }
            fileOutStream.close()
            zipInputStream.closeEntry()
        }
        zipInputStream.close()
        decipheredFile.delete()
    }
}