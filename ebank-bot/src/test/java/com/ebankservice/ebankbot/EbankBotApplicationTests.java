package com.ebankservice.ebankbot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.autoconfigure.exclude=com.zgamelogic.discord.components.DiscordBot")
class EbankBotApplicationTests {

    @Test
    void contextLoads() {
    }

}
