package nl.openminetopia.modules.portal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PortalModuleTest {

    @Test
    void bareHostUsesHttps() {
        assertEquals("https://portal.openminetopia.nl", PortalModule.resolveBaseUrl("portal.openminetopia.nl"));
        assertEquals("https://westvale.mtportal.nl", PortalModule.resolveBaseUrl("westvale.mtportal.nl"));
    }

    @Test
    void bareHostWithPortUsesHttps() {
        assertEquals("https://portal.example.nl:8443", PortalModule.resolveBaseUrl("portal.example.nl:8443"));
    }

    @Test
    void httpsUrlIsUsedAsGiven() {
        assertEquals("https://portal.example.nl", PortalModule.resolveBaseUrl("https://portal.example.nl"));
    }

    @Test
    void httpUrlWithPortIsUsedAsGiven() {
        assertEquals("http://203.0.113.10:8080", PortalModule.resolveBaseUrl("http://203.0.113.10:8080"));
    }

    @Test
    void schemeIsMatchedCaseInsensitively() {
        assertEquals("HTTP://203.0.113.10:8080", PortalModule.resolveBaseUrl("HTTP://203.0.113.10:8080"));
    }

    @Test
    void testHostUsesHttp() {
        assertEquals("http://openminetopia.test", PortalModule.resolveBaseUrl("openminetopia.test"));
        assertEquals("http://openminetopia.test:8000", PortalModule.resolveBaseUrl("openminetopia.test:8000"));
    }

    @Test
    void trailingSlashIsStripped() {
        assertEquals("https://portal.openminetopia.nl", PortalModule.resolveBaseUrl("portal.openminetopia.nl/"));
        assertEquals("http://203.0.113.10:8080", PortalModule.resolveBaseUrl("http://203.0.113.10:8080/"));
        assertEquals("https://portal.example.nl/sub", PortalModule.resolveBaseUrl(" https://portal.example.nl/sub// "));
    }

    @Test
    void detectsLocalDevelopmentHost() {
        assertTrue(PortalModule.isLocalDevelopmentHost("openminetopia.test"));
        assertTrue(PortalModule.isLocalDevelopmentHost("openminetopia.test:8000"));
        assertTrue(PortalModule.isLocalDevelopmentHost("http://openminetopia.test/"));
        assertFalse(PortalModule.isLocalDevelopmentHost("portal.openminetopia.nl"));
        assertFalse(PortalModule.isLocalDevelopmentHost("http://203.0.113.10:8080"));
        assertFalse(PortalModule.isLocalDevelopmentHost("https://test.example.nl"));
        assertFalse(PortalModule.isLocalDevelopmentHost(null));
    }
}
