package me.cereal.utility.setting;

import com.google.gson.JsonParser;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import me.cereal.utility.setting.converter.Convertable;

/**
 * Created by 086 on 12/10/2018.
 */
public abstract class Setting<T> implements ISettingUnknown, Convertable<T> {

    private boolean bval;

    String name;

    T value;

    /**
     * Returns false if the value is "out of bounds"
     */
    private Predicate<T> restriction;

    private Predicate<T> visibilityPredicate;

    private BiConsumer<T, T> consumer;

    private final Class valueType;

    public Setting(T value, Predicate<T> restriction, BiConsumer<T, T> consumer, String name, Predicate<T> visibilityPredicate) {
        this.value = value;
        this.valueType = value.getClass();
        this.restriction = restriction;
        this.consumer = consumer;
        this.name = name;
        this.visibilityPredicate = visibilityPredicate;
    }

    @Override
    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    @Override
    public Class getValueClass() {
        return valueType;
    }

    public boolean getValBoolean(){
        return this.bval;
    }

    public void setValBoolean(boolean in){
        this.bval = in;
    }

    /**
     * @param value
     * @return true if value was set
     */
    public boolean setValue(T value) {
        T old = getValue();
        if (!restriction.test(value))
            return false;
        this.value = value;
        consumer.accept(old, value);
        return true;
    }

    @Override
    public boolean isVisible() {
        return visibilityPredicate.test(getValue());
    }

    /**
     * @return A consumer that expects first the previous value and then the new value
     */
    public BiConsumer<T, T> changeListener() {
        return consumer;
    }

    @Override
    public void setValueFromString(String value) {
        JsonParser jp = new JsonParser();
        setValue(this.converter().reverse().convert(jp.parse(value)));
    }

    @Override
    public String getValueAsString() {
        return this.converter().convert(getValue()).toString();
    }
}
