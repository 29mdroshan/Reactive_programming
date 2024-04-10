package com.reactivespring.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Review {

    @Id
    private String reviewId;
    @NotNull(message = "rating.movieInfoId : must not be null")
    private String movieInfoId;
    private String comment;
    @Min(value = 0L, message = "rating.negative : please pass a non-negative value")
    @Max(value=10L,message = "rating cannot exceed 10 : please pass a rating below 10 value" )
    private Double rating;
}
