package com.bog.ecommerce.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    @NotEmpty(message = "პროდუქტის სახელი არუნდა იყოს ცარიელი")
    private String name;
    @Min(value = 0,message = "გთხოვთ მიუთითოთ რაოდენოება 0 ან მეტი")
    private int quantity;
    //@Range(min = 1, max = 4999,message = "make sure you type correct number between 1-5000")
    @Digits(integer = 4, fraction = 2, message = "მაქსიმალური ციფრების რაოდენობა 4 ფორმატი განაყოფით 2")
    @NotNull(message = "პროდუქტის ფასი არ უნდა იყოს ცარიელი")
    private BigDecimal price;
    private MultipartFile image;

}
