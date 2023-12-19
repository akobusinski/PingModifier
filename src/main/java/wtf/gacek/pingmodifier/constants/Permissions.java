package wtf.gacek.pingmodifier.constants;

public enum Permissions {
    RELOAD_CONFIG("pingmodifier.reload");

    private final String permission;
    Permissions(String permission) {
        this.permission = permission;
    }
    public String getPermission() {
        return permission;
    }
}
