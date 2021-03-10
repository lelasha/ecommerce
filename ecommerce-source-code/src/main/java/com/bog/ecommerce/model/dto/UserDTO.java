package com.bog.ecommerce.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    @NotEmpty(message = "გთხოვთ მიუთითოთ მაილი")
    @Email(message = "გთხოვთ მიუთითოთ სწორი მაილი")
    private String email;

    @NotEmpty(message = "გთხოვთ მიუთითოთ სახელი")
    private String firstName;

    @NotEmpty(message = "გთხოვთ მიუთითოთ გვარი")
    private String lastName;

    @NotEmpty(message = "გთხოვთ მიუთითოთ პაროლი")
    private String password;

    @Pattern(regexp = "\\d{11}", message = "გთხოვთ მიუთითოთ სწორი პირადი ნომერი")
    private String personalId;

    @Pattern(regexp = "^GE\\d{2}[A-Z]{2}\\d{16}$", message = "გთხოვთ მიუთითოთ სწორი ანგარიშის ნომერი")
    private String IBAN;
}
