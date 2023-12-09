/*
 * Copyright 2011-2013 Jeroen Meetsma - IJsberg
 *
 * This file is part of Iglu.
 *
 * Iglu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Iglu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Iglu.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.ijsberg.iglu.util.misc;


import org.junit.Assert;
import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class EncodingSupportTest {

    @Test
    public void encodeXor() {
    }

    @Test
    public void testDeflateInflate() throws Exception {
        String samlRequest = "<samlp:AuthnRequest\n" +
                "\txmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\"\n" +
                "\txmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\"\n" +
                "\tID=\"ID_12345\"" +
                "\tVersion=\"2.0\"\n" +
                "\tIssueInstant=\"2004-12-05T09:21:59Z\"\n" +
                "\tAssertionConsumerServiceIndex=\"0\"\n" +
                "\tAttributeConsumingServiceIndex=\"0\">\n" +
                "\t<saml:Issuer>https://sp.example.com/SAML2</saml:Issuer>\n" +
                "\t<samlp:NameIDPolicy  AllowCreate=\"true\"  Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:transient\"/>\n" +
                "</samlp:AuthnRequest>";

        byte[] samlRequestBytes = samlRequest.getBytes(StandardCharsets.UTF_8);
        byte[] deflatedBytes = EncodingSupport.deflate(samlRequestBytes);
        byte[] inflatedBytes = EncodingSupport.inflate(deflatedBytes);

        String samlRequestDeflatedInflated = new String(inflatedBytes, StandardCharsets.UTF_8);

        Assert.assertEquals(samlRequest, samlRequestDeflatedInflated);
    }

    @Test
    public void testDeflateInflateBigString() throws Exception {
        byte[] randomByteArray = new byte[10000];
        Random random = new Random();
        random.nextBytes(randomByteArray);
        String generatedString = new String(randomByteArray, StandardCharsets.UTF_8);

        byte[] deflatedBytes = EncodingSupport.deflate(generatedString.getBytes(StandardCharsets.UTF_8));
        byte[] inflatedBytes = EncodingSupport.inflate(deflatedBytes);

        Assert.assertEquals(generatedString, new String(inflatedBytes, StandardCharsets.UTF_8));
    }
}

