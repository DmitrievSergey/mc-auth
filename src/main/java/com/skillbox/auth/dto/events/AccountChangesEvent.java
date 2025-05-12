package com.skillbox.auth.dto.events;

public class AccountChangesEvent {
    private AccountChanges accountChanges;

    public AccountChangesEvent(AccountChanges accountChanges) {
        this.accountChanges = accountChanges;
    }

    public AccountChanges getAccountChanges() {
        return accountChanges;
    }

    public void setAccountChanges(AccountChanges accountChanges) {
        this.accountChanges = accountChanges;
    }
}
