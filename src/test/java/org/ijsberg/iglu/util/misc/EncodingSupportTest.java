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

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
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

    @Test
    public void test() throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        CertificateFactory fact = CertificateFactory.getInstance("X.509");
        String cert = "MIIC/TCCAeWgAwIBAgIIUd7j/OIahkYwDQYJKoZIhvcNAQELBQAwLTErMCkGA1UEAxMiYWNjb3VudHMuYWNjZXNzY29udHJvbC53aW5kb3dzLm5ldDAeFw0yMzExMDExNjAzMjdaFw0yODExMDExNjAzMjdaMC0xKzApBgNVBAMTImFjY291bnRzLmFjY2Vzc2NvbnRyb2wud2luZG93cy5uZXQwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCzZMJFMHQcoR8sE+Lf/wLEJtaKvPuuW/Nxeen/SPeOuZv+Gy3SKIeJ9IHATQVXVZbv0rLDQABOQt9IsvKWXIK7OChQ6CZd3dgxqoHyZA4Eh5wVAMAeQzWzLOL9XBV0m3wfXIjSE4Zw6S26MM8eJ1UW066gOoBaUKzuQIbCVrMrhJ+7+md8kjhGZTwC+o7Rq4ZzGDbggJuk/TUbQ+Bu9by6FZJZJJNeZ90iHnrsk4eyC8WvSbUBRC/vBt5HGDKIfCfb2HqDVvBJgkHgjpMwb5wPKC9T2U1YXN5iG2obhn9wDeSFYgyZOrd1XMKyLiJTfT35zQWilZpxMei4fIxFykkVAgMBAAGjITAfMB0GA1UdDgQWBBRNcCE3HDX+HOJOu/bKfLYoSX3/0jANBgkqhkiG9w0BAQsFAAOCAQEAExns169MDr1dDNELYNK0JDjPUA6GR50jqfc+xa2KOljeXErOdihSvKgDS/vnDN6fjNNZuOMDyr6jjLvRsT0jVWzf/B6v92FrPRa/rv3urGXvW5am3BZyVPipirbiolMTuork95G7y7imftK7117uHcMq3D8f4fxscDiDXgjEEZqjkuzYDGLaVWGJqpv5xE4w+K4o2uDwmEIeIX+rI1MEVucS2vsvraOrjqjHwc3KrzuVRSsOU7YVHyUhku+7oOrB4tYrVbYYgwd6zXnkdouVPqOX9wTkc9iTmbDP+rfkhdadLxU+hmMyMuCJKgkZbWKFES7ce23jfTMbpqoHB4pgtQ==";
        X509Certificate cer = (X509Certificate) fact.generateCertificate(new ByteArrayInputStream(EncodingSupport.decodeBase64(cert)));
        PublicKey key = cer.getPublicKey();

        final Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(key);

//        String signatureString = "lgICO4MlLnTh3PpHUtGoIhg58ubwHCSTQaxqPB5zlVcagLAJjPo528bZlq5AlAgsaxALUomL/sXIvpPwF25UKyiCVhLjbfRjERc0Pwv4AyGSwmJoNWC9Hc2A+Z5gtqX+xqamGk9rEaDSDTKXI1CYKXZBy+XzeeMb+zF08Vw12nUjdqHf2AXnQHnSkebH3kkRfljZeVK9NRfCnWZwrl6fZ6TAlu3JKuiwsXCBjRDOoyOE5P4F5CvbWfA17EWt+jIhLsTxVFM01GuGDDMiVehe02l+uLvTFYwhv201CI5FswhRnkv8u4Q54CXYXt2Y0aaOxhjwC2ijKIkLONb2pLrJZA==";
        String signatureString = "Prlc9kOFC+EBLBT79uXOdfwYEaFNSatU0f9cxQ9NJIjaChMJQfDSS0lQSX5r7EBbii1QCF6rT9aA/Y3o91OSCUVisbUVmVI1565Hhsx/GdQSKY/hFnQVB0aKuPCtTfX9Vo7tzR7ecz/C1NcDFkTSlRN65BSq/zTy7r+ifVoTLYKn1tvSNJPUmgZLRKMg+Q0bYLC9Ycrokcih4vNq5GBSBvAEOCrmOWRUFx/mg3WPq8Ya/St6OSAUBMOmCb6SAp+MzZnIH/eqNxmXE+6QEGiVn4dbW1NrCzF5E7WQu7py/osdUzZ3cuYO4aEh3VdBD7VarfQ2T9a29qXZLKq2v2uz5w==";
        System.out.println(sig.verify(EncodingSupport.decodeBase64(signatureString)));
    }
}

