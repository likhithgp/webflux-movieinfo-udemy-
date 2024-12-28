package com.reactivespring.moviesinfoservice.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class MovieInfo {

    @Id
    private String movieInfoId;

    @NotBlank(message = "Movie Name can't be empty")
    private String name;

    @NotNull
    @Positive(message = "Enter valid Positive value for year")
    private Integer year;


    private List<@NotBlank(message = "cast list can't be empty") String> cast;

    private LocalDate release_date;
}
