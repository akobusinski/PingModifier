package wtf.gacek.pingmodifier.cache;

import java.util.Objects;

public class HostnameProtocol {
    private final String hostname;
    private final int protocolVersion;

    public HostnameProtocol(String hostname, int protocol) {
        this.hostname = hostname;
        this.protocolVersion = protocol;
    }

    public String getHostname() {
        return hostname;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HostnameProtocol that = (HostnameProtocol) o;
        return protocolVersion == that.protocolVersion && Objects.equals(hostname, that.hostname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hostname, protocolVersion);
    }
}
