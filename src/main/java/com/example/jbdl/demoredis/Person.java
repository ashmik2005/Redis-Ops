package com.example.jbdl.demoredis;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Person implements Serializable {
    // Serialize :  Convert  Object ---> byte[]
    // Deserialize: Convert  byte[] ---> Object

    @Positive
    private int id;
    private int age;

    @NotBlank
    private String name;
    private double creditScore;


}
