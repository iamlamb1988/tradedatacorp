package tradedatacorp.item.stick.primitive;

import tradedatacorp.item.stick.Stick;

/**
 * A marker interface indicating the implementing class is a generic intraday candlestick.
 * Indicates datatypes will be stored as primitives.
 * Advantages: Significantly increase performance and computational speed and power
 * Disadvantages: Not as flexible and scalable compared to a generic implementation.
 * At the time of developing, primitives sticks should not have many modifications nor new features.
 */
public interface PrimitiveStick extends Stick{}