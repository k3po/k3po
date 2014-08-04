package org.kaazing.robot.driver.control;

public class BadRequestMessage extends ControlMessage {

    private String content;

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Override
    public Kind getKind() {
        return Kind.BAD_REQUEST;
    }

    @Override
    public int hashCode() {
        return super.hashTo();
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || (obj instanceof BadRequestMessage) && equals((BadRequestMessage) obj);
    }

    protected final boolean equals(BadRequestMessage that) {
        return super.equalTo(that);
    }
}
