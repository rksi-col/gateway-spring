package com.example.api_gateway.controller

import com.example.api_gateway.service.generatePdf
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.devh.boot.grpc.client.inject.GrpcClient
import org.example.proto.*
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.io.ByteArrayOutputStream
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/api/reports")
class ReportController(
    @GrpcClient("trainings-service")
    private val stub: TrainingsServiceGrpc.TrainingsServiceBlockingStub
) {

    /**
     * Экспорт отчёта в PDF
     * GET /api/reports/pdf?from=...&to=...
     */
    @GetMapping("/pdf")
    fun exportPdf(
        request: HttpServletRequest,                    // ← Добавь!
        @RequestParam from: Long,
        @RequestParam to: Long,
        response: HttpServletResponse
    ) {
        val accountId = request.getAttribute("accountId") as Long  // ← Из токена!

        val grpcRequest = GetTrainingReq.newBuilder()
            .setAccountId(accountId)
            .setRange(GetTrainingReq.TimestampRange.newBuilder()
                .setFrom(from)
                .setTo(to)
                .build())
            .build()

        val trainings = stub.getTraining(grpcRequest).trainingsList
        val pdfBytes = generatePdf(trainings, from, to)

        response.contentType = "application/pdf"
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=trainings-report.pdf")
        response.outputStream.write(pdfBytes)
        response.outputStream.flush()
    }

}