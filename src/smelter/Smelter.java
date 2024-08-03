/**
 * @author Bruce Lamb
 * @since 8 AUG 2024
 */

package tradedatacorp.smelter;

public interface Smelter<InputT, RefinedT, ParamT>{
    public void smelt(InputT rawData);
    public RefinedT fetch(ParamT criteria);
}