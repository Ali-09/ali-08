package com.example.ali_08.controller;

import com.example.ali_08.dto.ApiResponse;
import com.example.ali_08.dto.RecordRequest;
import com.example.ali_08.model.Record;
import com.example.ali_08.service.RecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Record>>> getAllRecords() {
        List<Record> records = recordService.getAllUserRecords();
        return ResponseEntity.ok(
            ApiResponse.success(records, "Movimientos obtenidos correctamente", HttpStatus.OK.value())
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Record>> createRecord(@Valid @RequestBody RecordRequest request) {
        Record record = recordService.createRecord(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.success(record, "Movimiento registrado correctamente", HttpStatus.CREATED.value())
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Record>> updateRecord(@PathVariable Long id, @Valid @RequestBody RecordRequest request) {
        Record record = recordService.updateRecord(id, request);
        return ResponseEntity.ok(
            ApiResponse.success(record, "Movimiento actualizado correctamente", HttpStatus.OK.value())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRecord(@PathVariable Long id) {
        recordService.deleteRecord(id);
        return ResponseEntity.ok(
            ApiResponse.success(null, "Movimiento eliminado correctamente", HttpStatus.OK.value())
        );
    }
}
