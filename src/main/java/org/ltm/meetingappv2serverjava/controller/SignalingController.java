package org.ltm.meetingappv2serverjava.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ltm.meetingappv2serverjava.DTO.SignalMessage;
import org.ltm.meetingappv2serverjava.service.SignalingService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
@RequiredArgsConstructor
public class SignalingController {

    private final SignalingService signalingService;
    /**
     * Handle join signal
     */
    @MessageMapping("/join")
    @SendTo("/topic/signals")
    public SignalMessage handleJoin(@Payload SignalMessage message) {
        log.info("Processing join signal from user: {}", message.getSenderId());
        return signalingService.handleJoin(message);
    }

    /**
     * Handle offer signal
     */
    @MessageMapping("/offer")
    @SendTo("/topic/signals")
    public SignalMessage handleOffer(@Payload SignalMessage message) {
        log.info("Processing offer signal from user: {} to user: {}", message.getSenderId(), message.getReceiverId());
        return signalingService.handleOffer(message);
    }

    /**
     * Handle answer signal
     */
    @MessageMapping("/answer")
    @SendTo("/topic/signals")
    public SignalMessage handleAnswer(@Payload SignalMessage message) {
        log.info("Processing answer signal from user: {} to user: {}", message.getSenderId(), message.getReceiverId());
        return signalingService.handleAnswer(message);
    }

    /**
     * Handle ICE candidate signal
     */
    @MessageMapping("/candidate")
    @SendTo("/topic/signals")
    public SignalMessage handleCandidate(@Payload SignalMessage message) {
        log.info("Processing ICE candidate from user: {} to user: {}", message.getSenderId(), message.getReceiverId());
        return signalingService.handleCandidate(message);
    }

    /**
     * Handle leave signal
     */
    @MessageMapping("/leave")
    @SendTo("/topic/signals")
    public SignalMessage handleLeave(@Payload SignalMessage message) {
        log.info("Processing leave signal from user: {}", message.getSenderId());
        return signalingService.handleLeave(message);
    }

    /**
     * Handle stop signal
     */
    @MessageMapping("/stop")
    @SendTo("/topic/signals")
    public SignalMessage handleStop(@Payload SignalMessage message) {
        log.info("Processing stop signal from user: {}", message.getSenderId());
        return signalingService.handleStop(message);
    }
}
