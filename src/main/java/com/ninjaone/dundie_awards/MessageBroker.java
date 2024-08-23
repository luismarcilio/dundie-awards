package com.ninjaone.dundie_awards;

import com.ninjaone.dundie_awards.model.Activity;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import org.springframework.stereotype.Component;

@Component
public class MessageBroker {

    private final Queue<Activity> messages = new LinkedBlockingDeque<>();

    public void addMessage(Activity message) {
        messages.add(message);
    }

    public List<Activity> getMessages() {
        return messages.stream().toList();
    }
}
