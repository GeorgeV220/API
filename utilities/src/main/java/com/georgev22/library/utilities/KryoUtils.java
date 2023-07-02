package com.georgev22.library.utilities;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import com.esotericsoftware.kryo.util.MapReferenceResolver;
import com.esotericsoftware.kryo.util.ObjectMap;
import com.georgev22.library.maps.*;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class KryoUtils {
    private final Kryo kryo;

    public KryoUtils() {
        kryo = createKryoInstance();
    }

    public byte[] serialize(Object object) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        kryo.writeClassAndObject(output, object);
        output.close();

        return byteArrayOutputStream.toByteArray();
    }

    public <T> T deserialize(byte[] bytes) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Input input = new Input(byteArrayInputStream);
        T object = (T) kryo.readClassAndObject(input);
        input.close();

        return object;
    }

    public void registerClass(Class<?> clazz) {
        kryo.register(clazz);
    }

    public void registerClass(Class<?> clazz, int id) {
        kryo.register(clazz, id);
    }

    public <T> void setDefaultSerializer(Class<? extends Serializer<T>> serializerClass) {
        kryo.setDefaultSerializer(serializerClass);
    }

    private @NotNull Kryo createKryoInstance() {
        Kryo kryo = new Kryo();
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        kryo.setReferenceResolver(new MapReferenceResolver());

        kryo.register(String.class);
        kryo.register(String[].class);

        kryo.register(Integer.class);
        kryo.register(Integer[].class);

        kryo.register(Double.class);
        kryo.register(Double[].class);

        kryo.register(Long.class);
        kryo.register(Long[].class);

        kryo.register(Boolean.class);
        kryo.register(Boolean[].class);

        kryo.register(BigDecimal.class);
        kryo.register(BigDecimal[].class);

        kryo.register(BigInteger.class);
        kryo.register(BigInteger[].class);

        kryo.register(Byte.class);
        kryo.register(Byte[].class);

        kryo.register(byte.class);
        kryo.register(byte[].class);

        kryo.register(Object.class);
        kryo.register(Object[].class);

        kryo.register(List.class);
        kryo.register(List[].class);


        kryo.register(Map.class);
        kryo.register(Map[].class);

        kryo.register(ObjectMap.class);
        kryo.register(ObjectMap[].class);
        kryo.register(ConcurrentObjectMap.class);
        kryo.register(ConcurrentObjectMap[].class);
        kryo.register(HashObjectMap.class);
        kryo.register(HashObjectMap[].class);
        kryo.register(LinkedObjectMap.class);
        kryo.register(LinkedObjectMap[].class);
        kryo.register(ObservableObjectMap.class);
        kryo.register(ObservableObjectMap[].class);
        kryo.register(TreeObjectMap.class);
        kryo.register(TreeObjectMap[].class);
        kryo.register(UnmodifiableObjectMap.class);
        kryo.register(UnmodifiableObjectMap[].class);


        kryo.setDefaultSerializer(DefaultSerializers.StringSerializer.class);

        return kryo;
    }
}
