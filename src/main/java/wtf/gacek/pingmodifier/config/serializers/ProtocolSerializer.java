package wtf.gacek.pingmodifier.config.serializers;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import wtf.gacek.pingmodifier.config.ProtocolVersion;
import wtf.gacek.pingmodifier.config.ServerMOTD;
import wtf.gacek.pingmodifier.math.Signs;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ProtocolSerializer implements TypeSerializer<ProtocolVersion> {
    @Override
    public ProtocolVersion deserialize(Type type, ConfigurationNode node) throws SerializationException {
        try {
            Signs sign = Signs.EQUAL;
            String rawVersion = node.getString();

            assert rawVersion != null;

            if (rawVersion.startsWith(Signs.MORE_OR_EQUAL.getSign())) {
                sign = Signs.MORE_OR_EQUAL;
                rawVersion = rawVersion.substring(Signs.MORE_OR_EQUAL.getSign().length());
            } else if (rawVersion.startsWith(Signs.LESS_OR_EQUAL.getSign())) {
                sign = Signs.LESS_OR_EQUAL;
                rawVersion = rawVersion.substring(Signs.LESS_OR_EQUAL.getSign().length());
            } else if (rawVersion.startsWith(Signs.MORE.getSign())) {
                sign = Signs.MORE;
                rawVersion = rawVersion.substring(Signs.LESS.getSign().length());
            } else if (rawVersion.startsWith(Signs.LESS.getSign())) {
                sign = Signs.LESS;
                rawVersion = rawVersion.substring(Signs.LESS.getSign().length());
            }
            return new ProtocolVersion(sign, Integer.parseInt(rawVersion));
        } catch (Exception e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public void serialize(Type type, @Nullable ProtocolVersion obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.raw(null);
            return;
        }

        node.set(obj.getVersionString());
    }
}
