package org.kaazing.specification.wse;

import java.nio.charset.Charset;
import java.util.Random;

import org.kaazing.k3po.lang.el.Function;
import org.kaazing.k3po.lang.el.spi.FunctionMapperSpi;


public class Functions {
	
	private static final Random RANDOM = new Random();
	
	@Function
    public static byte[] uniqueId() {
		byte[] bytes = new byte[16];
		RANDOM.nextBytes(bytes);
        return Base64.encode(bytes);
    }
	
	public static class Mapper extends FunctionMapperSpi.Reflective {

        public Mapper() {
            super(Functions.class);
        }

        @Override
        public String getPrefixName() {
            return "wse";
        }

    }

    private Functions() {
        // utility
    }
}
