package yz;

import lombok.Data;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

@Data
public class Message {
    private int id = new Random().nextInt();
    private String name = UUID.randomUUID().toString();
    private Date date = new Date();
}
