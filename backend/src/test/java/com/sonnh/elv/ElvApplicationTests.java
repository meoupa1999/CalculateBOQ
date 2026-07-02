package com.sonnh.elv;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ElvApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void myTest() {
        int quantity = 28;
        int subquantity = quantity;
        int count = 0;
        int width = 52;
        int height = 4;
        int condition = 70;
        int total = width;
        int pivot = 0;
        while (total < condition) {
            total += height;
            if (total <= condition) {
                pivot++;
            }
            System.out.println("total: " + total);
        }
        System.out.println("pivot: " + pivot);

        while (count <= quantity) {
            subquantity -= (pivot * 2) + 1;
            if (subquantity < 0) {
                count += ((quantity - count) / 2) + 1;
                System.out.println("count2: " + count);
                break;
            }
            count += pivot;
            System.out.println("count: " + count);
            count += pivot - 1;
        }
    }
}
