package com.skillbox.auth.dto.events;

public class ChangeEmailEvent {
    private ChangeEmail changeEmail;

    public ChangeEmailEvent() {
    }

    public ChangeEmailEvent(ChangeEmail changeEmail) {
        this.changeEmail = changeEmail;
    }

    public ChangeEmail getChangeEmail() {
        return changeEmail;
    }

    public void setChangeEmail(ChangeEmail changeEmail) {
        this.changeEmail = changeEmail;
    }
}
