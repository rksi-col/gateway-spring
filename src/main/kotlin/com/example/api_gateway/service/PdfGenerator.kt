package com.example.api_gateway.service

import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import org.example.proto.GetTrainingResp
import java.io.ByteArrayOutputStream
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun generatePdf(trainings: List<GetTrainingResp.Training>, from: Long, to: Long): ByteArray {
    val baos = ByteArrayOutputStream()
    val writer = PdfWriter(baos)
    val pdf = PdfDocument(writer)
    val document = Document(pdf)

    // Подключаем шрифт с поддержкой кириллицы
    val font = PdfFontFactory.createFont(
        "src/main/resources/fonts/DejaVuSans.ttf",
        PdfEncodings.IDENTITY_H
    )
    val boldFont = PdfFontFactory.createFont(
        "src/main/resources/fonts/DejaVuSans-Bold.ttf",
        PdfEncodings.IDENTITY_H
    )

    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        .withZone(ZoneId.systemDefault())

    // Заголовок
    document.add(
        Paragraph("Отчёт по тренировкам")
            .setTextAlignment(TextAlignment.CENTER)
            .setFont(boldFont)
            .setFontSize(18f)
    )

    document.add(
        Paragraph("Период: ${formatter.format(Instant.ofEpochMilli(from))} — ${formatter.format(Instant.ofEpochMilli(to))}")
            .setTextAlignment(TextAlignment.CENTER)
            .setFont(font)
            .setFontSize(10f)
    )

    document.add(Paragraph("\n"))

    // Таблица
    val table = Table(UnitValue.createPercentArray(floatArrayOf(25f, 20f, 15f, 40f)))
    table.setWidth(UnitValue.createPercentValue(100F))

    // Заголовки таблицы
    table.addHeaderCell(Paragraph("Дата").setFont(boldFont).setFontSize(10f))
    table.addHeaderCell(Paragraph("Категория").setFont(boldFont).setFontSize(10f))
    table.addHeaderCell(Paragraph("Упр.").setFont(boldFont).setFontSize(10f))
    table.addHeaderCell(Paragraph("Упражнения").setFont(boldFont).setFontSize(10f))

    // Данные
    trainings.forEach { training ->
        val date = formatter.format(Instant.ofEpochMilli(training.timestamp))
        table.addCell(Paragraph(date).setFont(font).setFontSize(9f))
        table.addCell(Paragraph(training.category).setFont(font).setFontSize(9f))
        table.addCell(Paragraph(training.exercisesCount.toString()).setFont(font).setFontSize(9f))
        table.addCell(
            Paragraph(
                training.exercisesList.joinToString(", ") { it.name }
            ).setFont(font).setFontSize(9f)
        )
    }

    document.add(table)

    // Статистика
    document.add(Paragraph("\n"))
    document.add(Paragraph("Статистика:").setFont(boldFont).setFontSize(14f))
    document.add(Paragraph("Всего тренировок: ${trainings.size}").setFont(font).setFontSize(11f))
    document.add(Paragraph("Всего упражнений: ${trainings.sumOf { it.exercisesCount }}").setFont(font).setFontSize(11f))

    // По категориям
    val categoryStats = trainings.groupBy { it.category }.mapValues { it.value.size }
    document.add(Paragraph("\nПо категориям:").setFont(boldFont).setFontSize(12f))
    categoryStats.forEach { (cat, count) ->
        document.add(Paragraph("  · $cat: $count").setFont(font).setFontSize(11f))
    }

    document.close()
    return baos.toByteArray()
}