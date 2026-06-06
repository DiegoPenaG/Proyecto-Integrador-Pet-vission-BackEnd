package com.petvission;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Requiere base de datos y SMTP configurados — ejecutar manualmente en entorno con .env")
class PetVissionApplicationTests {

    @Test
    void contextLoads() {
    }

}
