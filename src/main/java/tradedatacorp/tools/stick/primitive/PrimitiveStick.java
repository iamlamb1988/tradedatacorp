/**
 * @author Bruce Lamb
 * @since 18 FEB 2025
 */
package tradedatacorp.tools.stick.primitive;

import tradedatacorp.tools.stick.Stick;

/**
 * A marker interface indicating the implementing class is a generic intraday candlestick.
 * Indicates datatypes will be stored as primitives.
 * Advantages: Significantly increase performance and computational speed and power
 * Disadvantages: Not as flexible and scalable compared to a generic implementation.
 * At the time of developing, primitives sticks should not have many modifications nor new features.
 */
public abstract interface PrimitiveStick extends Stick{}