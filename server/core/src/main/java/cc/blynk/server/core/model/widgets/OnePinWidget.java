package cc.blynk.server.core.model.widgets;

import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.utils.JsonParser;
import cc.blynk.utils.StringUtils;
import io.netty.channel.Channel;

import static cc.blynk.server.core.protocol.enums.Command.*;
import static cc.blynk.utils.ByteBufUtil.*;
import static cc.blynk.utils.StringUtils.*;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 02.12.15.
 */
//todo all this should be replaced with 1 Pin field.
public abstract class OnePinWidget extends Widget {

    public PinType pinType;

    public byte pin = -1;

    public boolean pwmMode;

    public boolean rangeMappingOn;

    public int min;

    public int max;

    public String value;

    protected static String makeHardwareBody(PinType pinType, byte pin, String value) {
        return String.valueOf(pinType.pintTypeChar) + 'w' + BODY_SEPARATOR_STRING + pin + BODY_SEPARATOR_STRING + value;
    }

    @Override
    public void sendSyncOnActivate(Channel appChannel, int dashId) {
        String body = makeHardwareBody();
        if (body != null) {
            appChannel.write(makeStringMessage(SYNC, 1111, dashId + StringUtils.BODY_SEPARATOR_STRING + body));
        }
    }

    public String makeHardwareBody() {
        if (pin == -1 || value == null || pinType == null) {
            return null;
        }
        return isPWMSupported() ? makeHardwareBody(PinType.ANALOG, pin, value) : makeHardwareBody(pinType, pin, value);
    }

    @Override
    public boolean updateIfSame(byte pin, PinType type, String value) {
        if (isSame(pin, type)) {
            this.value = value;
            return true;
        }
        return false;
    }

    public boolean isSame(byte pin, PinType type) {
        return this.pin == pin && ((this.isPWMSupported() && type == PinType.ANALOG) || (type == this.pinType));
    }

    @Override
    public String getValue(byte pin, PinType type) {
        return value;
    }

    @Override
    public boolean hasValue(String searchValue) {
        return searchValue.equals(value);
    }

    @Override
    public String getJsonValue() {
        if (value == null) {
            return "[]";
        }
        return JsonParser.valueToJsonAsString(value);
    }

    public boolean isPWMSupported() {
        return false;
    }

}
