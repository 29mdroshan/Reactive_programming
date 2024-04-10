package com.reactivespring.model;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document //entity

public class MovieInfo {
	@Id
	private String movieInfoId;
	@NotBlank(message="movieInfo.name must be present")
	private String name;
	@NotNull
	@Positive(message="movieInfo.year must be a Positve value")
	private Integer year;
	private List< @NotBlank(message="movieInfo.cast must be a present") String> cast;
	private LocalDate release_date;
	

}
