package wtf.gacek.pingmodifier.config;

import wtf.gacek.pingmodifier.math.Signs;

public class ProtocolVersion {
    public ProtocolVersion(Signs sign, int version) {
        this.sign = sign;
        this.version = version;
    }
    private final Signs sign;
    private final int version;

    public Signs getSign() {
        return sign;
    }

    public int getVersion() {
        return version;
    }
    public String getVersionString() {
        return (this.sign == Signs.EQUAL ? "" : this.sign.getSign()) + version;
    }

    public boolean contains(int protocol) {
        if (this.sign == Signs.MORE_OR_EQUAL) {
            return protocol <= this.version;
        } else if (this.sign == Signs.LESS_OR_EQUAL) {
            return protocol >= this.version;
        } else if (this.sign == Signs.MORE) {
            return protocol < this.version;
        } else if (this.sign == Signs.LESS) {
            return protocol > this.version;
        } else {
            return this.version == protocol;
        }
    }
}
