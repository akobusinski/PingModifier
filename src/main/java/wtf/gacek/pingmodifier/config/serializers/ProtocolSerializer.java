package wtf.gacek.pingmodifier.config.serializers;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import wtf.gacek.pingmodifier.PingModifier;
import wtf.gacek.pingmodifier.config.ProtocolVersion;
import wtf.gacek.pingmodifier.math.Signs;

import java.lang.reflect.Type;

public class ProtocolSerializer implements TypeSerializer<ProtocolVersion> {
    @Override
    public ProtocolVersion deserialize(Type type, ConfigurationNode node) throws SerializationException {
        try {
            Signs sign = Signs.EQUAL;
            String rawVersion = node.getString();

            assert rawVersion != null;

            for (Signs theSign: Signs.values()) {
                if (theSign == Signs.EQUAL) continue; // this will never happen

                if (rawVersion.startsWith(theSign.getSign())) {
                    sign = theSign;
                    rawVersion = rawVersion.substring(sign.getSign().length());
                    break;
                }
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
