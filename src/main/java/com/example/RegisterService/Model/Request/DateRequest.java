package com.example.RegisterService.Model.Request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateRequest {
    @NotNull(message = "Start date cannot be null")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date fromDate;
    @NotNull(message = "End date cannot be null")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date toDate;
}
