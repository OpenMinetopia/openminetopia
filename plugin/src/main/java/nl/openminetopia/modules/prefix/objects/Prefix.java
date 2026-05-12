package nl.openminetopia.modules.prefix.objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Prefix {

    private int id;
    private String prefix;
    private long expiresAt;

    public Prefix(String prefix, long expiresAt) {
        this.prefix = prefix;
        this.expiresAt = expiresAt;
    }

    public Prefix(int id, String prefix, long expiresAt) {
        this.id = id;
        this.prefix = prefix;
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        return expiresAt != -1 && System.currentTimeMillis() >= expiresAt;
    }
}
