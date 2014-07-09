package org.kaazing.robot.lang.ast;

import org.kaazing.robot.lang.ast.value.AstValue;
import static org.kaazing.robot.lang.ast.util.AstUtil.equivalent;

public abstract class AstOptionNode extends AstStreamableNode {

    private String optionName;
    private AstValue optionValue;

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    public AstValue getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(AstValue optionValue) {
        this.optionValue = optionValue;
    }

    protected int hashTo() {
        int hashCode = super.hashTo();

        if (optionName != null) {
            hashCode <<= 4;
            hashCode ^= optionName.hashCode();
        }

        if (optionValue != null) {
            hashCode <<= 4;
            hashCode ^= optionValue.hashCode();
        }

        return hashCode;
    }

    protected boolean equalTo(AstOptionNode that) {
        return equivalent(this.optionName, that.optionName) && equivalent(this.optionValue, that.optionValue);
    }
}