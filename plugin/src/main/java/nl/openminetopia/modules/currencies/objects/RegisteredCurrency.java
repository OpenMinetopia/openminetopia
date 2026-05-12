package nl.openminetopia.modules.currencies.objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@RequiredArgsConstructor
public class RegisteredCurrency {

    private final String id;
    private final String displayName;
    private final String command;
    private final List<String> aliases;

    private boolean automatic = false;
    private int interval;
    private double amount;

}
