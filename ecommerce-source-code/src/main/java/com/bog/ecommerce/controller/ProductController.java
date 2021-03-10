package com.bog.ecommerce.controller;

import com.bog.ecommerce.exception.CustomException;
import com.bog.ecommerce.model.Product;
import com.bog.ecommerce.model.dto.ProductDTO;
import com.bog.ecommerce.model.response.ApiError;
import com.bog.ecommerce.repository.ProductOrderRepo;
import com.bog.ecommerce.repository.ProductRepo;
import com.bog.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@Validated
public class ProductController {

    private static final String MASTER_CARD_REGEX =
            "^5[1-5][0-9]{14}|"
            + "^(222[1-9]|22[3-9]\\d|"
            + "2[3-6]\\d{2}|27[0-1]\\d|"
            + "2720)[0-9]{12}$";

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private ProductOrderRepo productOrderRepo;




    @GetMapping("/api/product/findall")
    public List<Product> products(){
        return productService.changePrice(productRepo.findAll());

    }

    @GetMapping("/auth/product/get/{productid}")
    public ResponseEntity<Object> products(@PathVariable Long productid){
        Optional<Product> product = productRepo.findById(productid);

        return product.<ResponseEntity<Object>>map(value -> ResponseEntity.ok(productService.changePrice(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/auth/product/findall/{userid}")
    public List<Product> userProducts(@PathVariable Long userid){
        return productService.changePrice(productRepo.findAllProductByUserId(userid));
    }


    @PostMapping("/auth/product/create")
    public ResponseEntity<?> createProduct
            (@ModelAttribute @Valid ProductDTO productDTO, Errors error) throws IOException, CustomException {
        Object auth = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return productService.saveProduct(productDTO, error,auth);
    }

    @PostMapping("/auth/product/update/{productid}")
    public ResponseEntity<?> updateProduct
            (@PathVariable Long productid, @ModelAttribute @Valid ProductDTO productDTO, Errors error) throws IOException, CustomException {

        Object auth = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(auth);
        return productService.updateProduct(productid, productDTO, error,auth);
    }

    @PostMapping("/auth/product/delete/{productid}")
    public ResponseEntity<?> deleteProduct
            (@PathVariable Long productid) throws IOException, CustomException {
        Object auth = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return productService.deleteProduct(productid,auth);
    }


    @PostMapping("/api/product/buy/{productid}")
    public ResponseEntity<?> buyProductForm(

            @PathVariable Long productid,
            @RequestParam @NotEmpty(message = "პირადი ნომრის ველი არუნდა იყოს ცარიელი")@Pattern(regexp = "\\d{11}", message = "გთხოვთ მიუთითოთ სწორი პირადი ნომერი") String personalid,
            @RequestParam @NotNull(message = "რაოდენობა არუნდა იყოს ცარიელი") @Positive(message = "არასწორი რაოდენობა") Integer quantity,
            @RequestParam @NotEmpty(message = "მაილი არუნდა იყოს ცარიელი") @Email(message = "გთხოვთ მიუთითოთ სწორი მაილი")  String email,
            @RequestParam
            @NotEmpty(message = "გთხოვთ მიუთითოთ თქვენი მასტერქარდის 16 ნიშნა რიცხვი")
            @Pattern(regexp = MASTER_CARD_REGEX,message = "გთხოვთ მიუთითოთ სწორი მასტერქარდის 16 ნიშნა რიცხვი") String mastercard
    ) {
        return productService.buyProduct(productid, personalid, quantity, email,mastercard.trim());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> onValidationError(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(ex.getMessage().trim().split(":")[1]));
    }


}
