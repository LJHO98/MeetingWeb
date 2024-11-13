package com.MeetingWeb.Excption;

public class TournamentAlreadyCompletedException extends Throwable {
    public TournamentAlreadyCompletedException(String message) {
        super(message);
    }
}
